package com.aicrm.web.platform;

import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.platform.application.FrontendDirectoryService;
import com.aicrm.sales.application.SalesReadService;
import com.aicrm.sales.domain.pool.PublicPool;
import com.aicrm.web.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FrontendDirectoryV1Controller {
    private final FrontendDirectoryService directory;
    private final SalesReadService sales;

    public FrontendDirectoryV1Controller(FrontendDirectoryService directory, SalesReadService sales) {
        this.directory = directory;
        this.sales = sales;
    }

    @GetMapping("/auth/me")
    public ApiResponse<CurrentUserView> me() {
        var user = directory.currentUser(ActorContext.require());
        return success(new CurrentUserView(id(user.tenantId()), id(user.userId()), user.username(), user.name(), id(user.departmentId()), user.departmentName(), user.roles(), user.permissions(), user.dataScopes().stream().map(Enum::name).toList()));
    }

    @GetMapping("/directory/users")
    public ApiResponse<List<UserView>> users() {
        return success(directory.users(ActorContext.require()).stream().map(user -> new UserView(id(user.id()), user.username(), user.name(), id(user.departmentId()), user.departmentName())).toList());
    }

    @GetMapping("/directory/departments")
    public ApiResponse<List<DepartmentView>> departments() {
        return success(directory.departments(ActorContext.require()).stream().map(department -> new DepartmentView(id(department.id()), department.code(), department.name(), id(department.parentId()), department.path())).toList());
    }

    @GetMapping("/directory/public-pools")
    public ApiResponse<List<PublicPoolView>> publicPools(@RequestParam PublicPool.ResourceType resourceType) {
        return success(sales.activePublicPools(ActorContext.require(), resourceType).stream().map(pool -> new PublicPoolView(id(pool.id()), pool.resourceType().name(), pool.code(), pool.name(), pool.claimEnabled(), pool.assignEnabled(), pool.releaseEnabled())).toList());
    }

    private <T> ApiResponse<T> success(T data) { return ApiResponse.success(data, TraceContext.get()); }
    private static String id(long value) { return String.valueOf(value); }
    private static String id(Long value) { return value == null ? null : String.valueOf(value); }
    record CurrentUserView(String tenantId, String userId, String username, String name, String departmentId, String departmentName, java.util.Set<String> roles, java.util.Set<String> permissions, List<String> dataScopes) { }
    record UserView(String id, String username, String name, String departmentId, String departmentName) { }
    record DepartmentView(String id, String code, String name, String parentId, String path) { }
    record PublicPoolView(String id, String resourceType, String code, String name, boolean claimEnabled, boolean assignEnabled, boolean releaseEnabled) { }
}
