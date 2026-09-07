package com.aicrm.web.sales;

import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.sales.application.SalesCommandService;
import com.aicrm.sales.application.SalesCommands;
import com.aicrm.sales.application.SalesReadService;
import com.aicrm.sales.domain.customer.Contact;
import com.aicrm.sales.domain.customer.Customer;
import com.aicrm.web.api.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerV1Controller {
    private final SalesReadService readService;
    private final SalesCommandService commandService;

    public CustomerV1Controller(SalesReadService readService, SalesCommandService commandService) {
        this.readService = readService;
        this.commandService = commandService;
    }

    @GetMapping("/private")
    public ApiResponse<SalesApiDtos.PageView<SalesApiDtos.CustomerView>> privateCustomers(
            @RequestParam(defaultValue = "1") @Min(1) long page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) long size) {
        return success(page(readService.privateCustomers(actor(), page, size)));
    }

    @GetMapping("/public")
    public ApiResponse<SalesApiDtos.PageView<SalesApiDtos.CustomerView>> publicCustomers(
            @RequestParam(defaultValue = "1") @Min(1) long page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) long size) {
        return success(page(readService.publicCustomers(actor(), page, size)));
    }

    @GetMapping("/{id}")
    public ApiResponse<SalesApiDtos.CustomerView> detail(@PathVariable long id) {
        return success(view(readService.customer(actor(), id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SalesApiDtos.CustomerView>> create(@Valid @RequestBody SalesApiDtos.CreateCustomerRequest request,
                                                                           @RequestHeader("Idempotency-Key") String idempotencyKey) {
        Customer customer = commandService.createCustomer(actor(), new SalesCommands.CreateCustomer(request.name(),
                request.industry(), request.region(), request.publicPoolId()), idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(view(customer)));
    }

    @PostMapping("/{id}/actions/claim")
    public ApiResponse<SalesApiDtos.CustomerView> claim(@PathVariable long id, @Valid @RequestBody SalesApiDtos.OwnershipRequest request) {
        return success(view(commandService.claimCustomer(actor(), id, ownership(id, request))));
    }

    @PostMapping("/{id}/actions/release")
    public ApiResponse<SalesApiDtos.CustomerView> release(@PathVariable long id, @Valid @RequestBody SalesApiDtos.OwnershipRequest request) {
        return success(view(commandService.releaseCustomer(actor(), id, ownership(id, request))));
    }

    @PostMapping("/{id}/actions/assign")
    public ApiResponse<SalesApiDtos.CustomerView> assign(@PathVariable long id, @Valid @RequestBody SalesApiDtos.OwnershipRequest request) {
        return success(view(commandService.transferCustomer(actor(), id, ownership(id, request), true)));
    }

    @PostMapping("/{id}/actions/transfer")
    public ApiResponse<SalesApiDtos.CustomerView> transfer(@PathVariable long id, @Valid @RequestBody SalesApiDtos.OwnershipRequest request) {
        return success(view(commandService.transferCustomer(actor(), id, ownership(id, request), false)));
    }

    @PostMapping("/{id}/actions/merge")
    public ResponseEntity<ApiResponse<Void>> merge(@PathVariable long id, @Valid @RequestBody SalesApiDtos.MergeRequest request,
                                                    @RequestHeader("Idempotency-Key") String idempotencyKey) {
        commandService.mergeCustomers(actor(), new SalesCommands.MergeCustomers(id, request.targetCustomerId(), request.version(), request.reason()),
                idempotencyKey);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null, TraceContext.get()));
    }

    @GetMapping("/{id}/contacts")
    public ApiResponse<List<SalesApiDtos.ContactView>> contacts(@PathVariable long id) {
        return success(readService.contacts(actor(), id).stream().map(CustomerV1Controller::view).toList());
    }

    @PostMapping("/{id}/contacts")
    public ResponseEntity<ApiResponse<SalesApiDtos.ContactView>> addContact(@PathVariable long id,
                                                                               @Valid @RequestBody SalesApiDtos.ContactRequest request) {
        Contact contact = commandService.createContact(actor(), id, new SalesCommands.CreateContact(request.name(), request.mobile(),
                request.email(), request.department(), request.title(), request.decisionMaker()));
        return ResponseEntity.status(HttpStatus.CREATED).body(success(view(contact)));
    }

    @PostMapping("/{id}/follow-ups")
    public ApiResponse<SalesApiDtos.CustomerView> followUp(@PathVariable long id, @Valid @RequestBody SalesApiDtos.FollowUpRequest request) {
        return success(view(commandService.addCustomerFollowUp(actor(), id, new SalesCommands.AddFollowUp(request.version(),
                request.channel(), request.content(), request.nextFollowUpAt()))));
    }

    private SalesCommands.OwnershipChange ownership(long id, SalesApiDtos.OwnershipRequest request) {
        return new SalesCommands.OwnershipChange(id, request.version(), request.targetUserId(), request.publicPoolId(), request.reason());
    }

    private SalesApiDtos.PageView<SalesApiDtos.CustomerView> page(PageResult<Customer> source) {
        List<SalesApiDtos.CustomerView> items = source.records().stream().map(CustomerV1Controller::view).toList();
        return new SalesApiDtos.PageView<>(items, source.page(), source.size(), source.total());
    }

    static SalesApiDtos.CustomerView view(Customer customer) {
        return new SalesApiDtos.CustomerView(LeadV1Controller.id(customer.id()), customer.customerNo(), customer.name(),
                customer.industry(), customer.region(), customer.status(), customer.ownershipType().name(),
                LeadV1Controller.id(customer.ownerUserId()), LeadV1Controller.id(customer.ownerDeptId()),
                LeadV1Controller.id(customer.publicPoolId()), customer.poolEnteredAt(), customer.lastFollowUpAt(),
                customer.nextFollowUpAt(), customer.version(), customer.createdAt(), customer.updatedAt());
    }

    static SalesApiDtos.ContactView view(Contact contact) {
        return new SalesApiDtos.ContactView(LeadV1Controller.id(contact.id()), LeadV1Controller.id(contact.customerId()), contact.name(),
                contact.mobile(), contact.email(), contact.department(), contact.title(), contact.decisionMaker(), contact.version());
    }

    private Actor actor() {
        return ActorContext.require();
    }

    private <T> ApiResponse<T> success(T data) {
        return ApiResponse.success(data, TraceContext.get());
    }
}
