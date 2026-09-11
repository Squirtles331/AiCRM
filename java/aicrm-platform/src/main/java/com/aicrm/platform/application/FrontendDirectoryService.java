package com.aicrm.platform.application;

import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.DataScope;
import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** Read-only tenant directory for CRM form selectors and navigation. */
@Service
public class FrontendDirectoryService {
    private final JdbcTemplate jdbc;

    public FrontendDirectoryService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public CurrentUser currentUser(Actor actor) {
        CurrentUser result = jdbc.query("select u.username,u.name,d.name from crm_user u join crm_department d on d.tenant_id=u.tenant_id and d.id=u.department_id where u.tenant_id=? and u.id=? and u.deleted_at is null and d.deleted_at is null",
                rs -> rs.next() ? new CurrentUser(actor.tenantId(), actor.userId(), rs.getString(1), rs.getString(2), actor.departmentId(), rs.getString(3), actor.roles(), actor.permissions(), actor.dataScopes()) : null,
                actor.tenantId(), actor.userId());
        if (result == null) throw new DomainException(ErrorCode.UNAUTHORIZED, "当前用户不存在或已停用");
        return result;
    }

    public List<UserOption> users(Actor actor) {
        StringBuilder sql = new StringBuilder("select u.id,u.username,u.name,u.department_id,d.name from crm_user u join crm_department d on d.tenant_id=u.tenant_id and d.id=u.department_id where u.tenant_id=? and u.status=1 and u.deleted_at is null and d.deleted_at is null");
        List<Object> args = new ArrayList<>(List.of(actor.tenantId()));
        appendScope(sql, args, actor, "u", "d");
        sql.append(" order by u.name,u.id");
        return jdbc.query(sql.toString(), (rs, row) -> new UserOption(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getLong(4), rs.getString(5)), args.toArray());
    }

    public List<DepartmentOption> departments(Actor actor) {
        StringBuilder sql = new StringBuilder("select id,code,name,parent_id,path from crm_department where tenant_id=? and status=1 and deleted_at is null");
        List<Object> args = new ArrayList<>(List.of(actor.tenantId()));
        if (!actor.hasDataScope(DataScope.ALL)) {
            if (actor.hasDataScope(DataScope.DEPARTMENT_AND_SUB)) { sql.append(" and path like ?"); args.add(actor.departmentPath() + "%"); }
            else if (actor.hasDataScope(DataScope.DEPARTMENT)) { sql.append(" and id=?"); args.add(actor.departmentId()); }
            else { sql.append(" and id=?"); args.add(actor.departmentId()); }
        }
        sql.append(" order by path,id");
        return jdbc.query(sql.toString(), (rs, row) -> new DepartmentOption(rs.getLong(1), rs.getString(2), rs.getString(3), (Long) rs.getObject(4), rs.getString(5)), args.toArray());
    }

    private void appendScope(StringBuilder sql, List<Object> args, Actor actor, String userAlias, String departmentAlias) {
        if (actor.hasDataScope(DataScope.ALL)) return;
        List<String> scopes = new ArrayList<>();
        if (actor.hasDataScope(DataScope.SELF)) { scopes.add(userAlias + ".id=?"); args.add(actor.userId()); }
        if (actor.hasDataScope(DataScope.DEPARTMENT)) { scopes.add(userAlias + ".department_id=?"); args.add(actor.departmentId()); }
        if (actor.hasDataScope(DataScope.DEPARTMENT_AND_SUB)) { scopes.add(departmentAlias + ".path like ?"); args.add(actor.departmentPath() + "%"); }
        sql.append(scopes.isEmpty() ? " and 1=0" : " and (" + String.join(" or ", scopes) + ")");
    }

    public record CurrentUser(long tenantId, long userId, String username, String name, long departmentId, String departmentName,
                              java.util.Set<String> roles, java.util.Set<String> permissions, java.util.Set<DataScope> dataScopes) { }
    public record UserOption(long id, String username, String name, long departmentId, String departmentName) { }
    public record DepartmentOption(long id, String code, String name, Long parentId, String path) { }
}
