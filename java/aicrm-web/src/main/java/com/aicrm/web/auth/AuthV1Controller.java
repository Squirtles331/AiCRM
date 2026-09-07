package com.aicrm.web.auth;

import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.platform.api.AccessTokenService;
import com.aicrm.platform.application.AuthenticationService;
import com.aicrm.web.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "CRM V1 - 身份认证")
public class AuthV1Controller {
    private final AuthenticationService authenticationService;
    private final AccessTokenService accessTokenService;

    public AuthV1Controller(AuthenticationService authenticationService, AccessTokenService accessTokenService) {
        this.authenticationService = authenticationService;
        this.accessTokenService = accessTokenService;
    }

    @PostMapping("/login")
    @Operation(summary = "租户用户登录")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Actor actor = authenticationService.authenticate(request.tenantId(), request.username(), request.password());
        return ApiResponse.success(new LoginResponse(accessTokenService.issue(actor), "Bearer",
                accessTokenService.expiresInSeconds()), TraceContext.get());
    }

    public record LoginRequest(@Positive long tenantId, @NotBlank String username, @NotBlank String password) {
    }

    public record LoginResponse(String accessToken, String tokenType, long expiresInSeconds) {
    }
}
