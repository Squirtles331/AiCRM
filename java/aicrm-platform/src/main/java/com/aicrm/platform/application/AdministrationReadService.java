package com.aicrm.platform.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/** Tenant administration queries. Business form options remain in FrontendDirectoryService. */
@Service
public class AdministrationReadService {
    private final JdbcTemplate jdbc;

    public AdministrationReadService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public PageResult<UserView> users(Actor actor, long page, long size, String keyword, Integer status) {
        requireAdministrator(actor);
        String filter = " where u.tenant_id=? and u.deleted_at is null"
                + (hasText(keyword) ? " and (lower(u.username) like lower(?) or lower(u.name) like lower(?))" : "")
                + (status == null ? "" : " and u.status=?");
        Object[] args = args(actor.tenantId(), keyword, status);
        Long total = jdbc.queryForObject("select count(*) from crm_user u" + filter, Long.class, args);
        String sql = "select u.id,u.username,u.name,u.mobile,u.email,u.status,u.department_id,d.name,u.created_at,"
                + "coalesce(string_agg(r.name, ',' order by r.name) filter (where r.id is not null),'') "
                + "from crm_user u join crm_department d on d.tenant_id=u.tenant_id and d.id=u.department_id "
                + "left join crm_user_role ur on ur.tenant_id=u.tenant_id and ur.user_id=u.id and ur.deleted_at is null "
                + "left join crm_role r on r.tenant_id=ur.tenant_id and r.id=ur.role_id and r.deleted_at is null"
                + filter + " group by u.id,u.username,u.name,u.mobile,u.email,u.status,u.department_id,d.name,u.created_at "
                + "order by u.created_at desc,u.id desc limit ? offset ?";
        Object[] pageArgs = append(args, size, (page - 1) * size);
        List<UserView> records = jdbc.query(sql, (rs, row) -> new UserView(String.valueOf(rs.getLong(1)), rs.getString(2), rs.getString(3),
                rs.getString(4), rs.getString(5), rs.getInt(6), String.valueOf(rs.getLong(7)), rs.getString(8),
                rs.getTimestamp(9).toInstant(), rs.getString(10)), pageArgs);
        return new PageResult<>(records, page, size, total == null ? 0 : total);
    }

    public PageResult<RoleView> roles(Actor actor, long page, long size, String keyword, Integer status) {
        requireAdministrator(actor);
        String filter = " where r.tenant_id=? and r.deleted_at is null"
                + (hasText(keyword) ? " and (lower(r.code) like lower(?) or lower(r.name) like lower(?))" : "")
                + (status == null ? "" : " and r.status=?");
        Object[] args = args(actor.tenantId(), keyword, status);
        Long total = jdbc.queryForObject("select count(*) from crm_role r" + filter, Long.class, args);
        String sql = "select r.id,r.code,r.name,r.status,r.data_scope,r.created_at,"
                + "(select count(*) from crm_user_role ur where ur.tenant_id=r.tenant_id and ur.role_id=r.id and ur.deleted_at is null),"
                + "(select count(*) from crm_role_permission rp where rp.tenant_id=r.tenant_id and rp.role_id=r.id and rp.deleted_at is null) "
                + "from crm_role r" + filter + " order by r.created_at desc,r.id desc limit ? offset ?";
        List<RoleView> records = jdbc.query(sql, (rs, row) -> new RoleView(String.valueOf(rs.getLong(1)), rs.getString(2), rs.getString(3),
                rs.getInt(4), rs.getString(5), rs.getTimestamp(6).toInstant(), rs.getLong(7), rs.getLong(8)),
                append(args, size, (page - 1) * size));
        return new PageResult<>(records, page, size, total == null ? 0 : total);
    }

    public List<DepartmentView> departments(Actor actor) {
        requireAdministrator(actor);
        return jdbc.query("select id,code,name,parent_id,path,status,created_at from crm_department "
                        + "where tenant_id=? and deleted_at is null order by path,id",
                (rs, row) -> new DepartmentView(String.valueOf(rs.getLong(1)), rs.getString(2), rs.getString(3),
                        nullableId((Long) rs.getObject(4)), rs.getString(5), rs.getInt(6), rs.getTimestamp(7).toInstant()),
                actor.tenantId());
    }

    public List<PermissionView> permissions(Actor actor) {
        requireAdministrator(actor);
        return jdbc.query("select code,name,resource_type,action,status,description from crm_permission "
                        + "where tenant_id=? and deleted_at is null order by resource_type,action,code",
                (rs, row) -> new PermissionView(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getInt(5), rs.getString(6)), actor.tenantId());
    }

    private void requireAdministrator(Actor actor) {
        if (!actor.roles().contains("admin")) {
            throw new DomainException(ErrorCode.FORBIDDEN, "仅租户管理员可访问系统管理");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private Object[] args(long tenantId, String keyword, Integer status) {
        if (hasText(keyword) && status != null) return new Object[]{tenantId, "%" + keyword.trim() + "%", "%" + keyword.trim() + "%", status};
        if (hasText(keyword)) return new Object[]{tenantId, "%" + keyword.trim() + "%", "%" + keyword.trim() + "%"};
        if (status != null) return new Object[]{tenantId, status};
        return new Object[]{tenantId};
    }

    private Object[] append(Object[] source, Object... values) {
        Object[] result = java.util.Arrays.copyOf(source, source.length + values.length);
        System.arraycopy(values, 0, result, source.length, values.length);
        return result;
    }

    private String nullableId(Long value) {
        return value == null ? null : String.valueOf(value);
    }

    public record UserView(String id, String username, String name, String mobile, String email, int status,
                           String departmentId, String departmentName, Instant createdAt, String roleNames) { }
    public record RoleView(String id, String code, String name, int status, String dataScope, Instant createdAt,
                           long userCount, long permissionCount) { }
    public record DepartmentView(String id, String code, String name, String parentId, String path, int status,
                                 Instant createdAt) { }
    public record PermissionView(String code, String name, String resourceType, String action, int status,
                                 String description) { }
}
