package com.aicrm.stagegate;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class DatabaseStageGateTest {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Test
    void emptyDatabaseMigratesThroughFrozenVersion() throws Exception {
        resetAndMigrate();

        assertEquals("24", scalar("select version from flyway_schema_history where success order by installed_rank desc limit 1"));
        assertEquals("50", scalar("select count(*) from information_schema.tables "
                + "where table_schema = 'public' and table_name like 'crm_%'"));
        assertEquals("4", scalar("select count(*) from pg_constraint "
                + "where conname in ('ck_crm_lead_ownership','ck_crm_customer_ownership',"
                + "'ck_crm_role_data_scope','ck_crm_idempotency_status')"));
    }

    @Test
    void predecessorTablesAreRemovedWithoutImportingTheirData() throws Exception {
        clean();
        migrateTo("1");
        execute("create table tenant (id bigint primary key, name varchar(200), status smallint, "
                + "created_at timestamptz, updated_at timestamptz, deleted smallint)");
        execute("create table users (id bigint primary key, tenant_id bigint, name varchar(100), "
                + "mobile varchar(32), email varchar(200), role_code varchar(50), status smallint, "
                + "created_at timestamptz, updated_at timestamptz, deleted smallint)");
        execute("insert into tenant values (91, 'predecessor', 1, now(), now(), 0)");
        execute("insert into users values (92, 91, 'predecessor-user', '13800000000', "
                + "'predecessor@example.com', 'admin', 1, now(), now(), 0)");

        migrate();

        assertEquals("0", scalar("select count(*) from information_schema.tables where table_schema='public' "
                + "and table_name in ('tenant','users')"));
        assertEquals("0", scalar("select count(*) from crm_tenant where id=91"));
        assertEquals("0", scalar("select count(*) from crm_user where id=92"));
    }

    @Test
    void frozenModelSupportsMilestoneTransactionsAndRejectsInvalidOnes() throws Exception {
        resetAndMigrate();
        seedPlatform();

        verifyTenantIsolationAndDataScope();
        verifyPoolAndOwnershipConstraints();
        verifyConcurrentClaimAndVersionConflict();
        verifyIdempotency();
        verifyLeadConversion();
        verifyCustomerMerge();
        verifyHandover();
        verifyImmutableHistoryAndRetryableOutbox();
    }

    private void verifyTenantIsolationAndDataScope() throws Exception {
        SQLException crossTenant = assertThrows(SQLException.class, () -> inTransaction(connection ->
                update(connection, "insert into crm_user_role (tenant_id,user_id,role_id) values (1,201,10001)")));
        assertEquals("23503", crossTenant.getSQLState());

        insertLead(2002, 1, "L-2002", "PRIVATE", 101L, null, "NEW", null);
        insertLead(2003, 1, "L-2003", "PRIVATE", 102L, null, "NEW", null);
        insertLead(2004, 2, "L-2004", "PRIVATE", 201L, null, "NEW", null);

        assertEquals("1", scalar("select count(*) from crm_lead where tenant_id=1 and owner_user_id=101"));
        assertEquals("2", scalar("select count(*) from crm_lead where tenant_id=1 and owner_dept_id in "
                + "(select id from crm_department where tenant_id=1 and path like '/11%')"));
        assertEquals("4", scalar("select count(distinct data_scope) from crm_role where tenant_id=1"));
    }

    private void verifyPoolAndOwnershipConstraints() throws SQLException {
        assertEquals("1", scalar("select count(*) from crm_user where tenant_id=1 and username='__system__' and status=1"));

        SQLException wrongPoolType = assertThrows(SQLException.class, () ->
                insertLead(2010, 1, "L-WRONG-POOL", "PUBLIC", null, 1002L, "NEW", null));
        assertEquals("23514", wrongPoolType.getSQLState());

        SQLException crossTenantOwner = assertThrows(SQLException.class, () ->
                insertLead(2011, 1, "L-WRONG-OWNER", "PRIVATE", 201L, null, "NEW", null));
        assertEquals("23514", crossTenantOwner.getSQLState());

        assertThrows(SQLException.class, () -> execute("update crm_public_pool set recycle_after_days=10 "
                + "where tenant_id=1 and id=1001"));
        SQLException duplicateAutoRecycleTarget = assertThrows(SQLException.class, () -> execute("insert into crm_public_pool "
                + "(id,tenant_id,resource_type,code,name,auto_recycle_enabled,recycle_after_days,created_by,updated_by) "
                + "values (1003,1,'LEAD','SECOND','第二线索公海',true,30,101,101)"));
        assertEquals("23505", duplicateAutoRecycleTarget.getSQLState());
    }

    private void verifyConcurrentClaimAndVersionConflict() throws Exception {
        insertLead(2001, 1, "L-2001", "PUBLIC", null, 1001L, "NEW", null);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Future<Integer>> results = new ArrayList<>();
        for (long userId : List.of(101L, 102L)) {
            results.add(executor.submit(() -> claimLead(userId, ready, start)));
        }
        ready.await();
        start.countDown();
        int affected = results.get(0).get() + results.get(1).get();
        executor.shutdownNow();

        assertEquals(1, affected);
        assertEquals("1", scalar("select version from crm_lead where id=2001"));
        assertEquals("1", scalar("select count(*) from crm_ownership_history "
                + "where resource_type='LEAD' and resource_id=2001 and action='CLAIM'"));

        assertEquals("0", scalar("with changed as (update crm_lead set version=version+1 "
                + "where tenant_id=1 and id=2001 and version=0 returning 1) select count(*) from changed"));
    }

    private int claimLead(long userId, CountDownLatch ready, CountDownLatch start) throws Exception {
        try (Connection connection = connection()) {
            connection.setAutoCommit(false);
            ready.countDown();
            start.await();
            int changed = update(connection, "update crm_lead set ownership_type='PRIVATE', owner_user_id=?, "
                    + "public_pool_id=null, pool_entered_at=null, version=version+1, updated_by=? "
                    + "where tenant_id=1 and id=2001 and ownership_type='PUBLIC' and public_pool_id=1001 "
                    + "and version=0 and deleted_at is null", userId, userId);
            if (changed == 1) {
                long suffix = userId;
                update(connection, "insert into crm_ownership_history "
                        + "(id,tenant_id,resource_type,resource_id,action,from_pool_id,to_owner_user_id,operator_user_id,operation_id) "
                        + "values (?,1,'LEAD',2001,'CLAIM',1001,?,?,?)",
                        910000 + suffix, userId, userId, "claim-" + suffix);
                audit(connection, 920000 + suffix, 1, userId, "LEAD_CLAIM", "LEAD", 2001, "claim-" + suffix);
                outbox(connection, 930000 + suffix, 1, "LEAD", 2001, "LeadClaimed", "claim-" + suffix);
            }
            connection.commit();
            return changed;
        }
    }

    private void verifyIdempotency() throws Exception {
        execute("insert into crm_idempotency_record "
                + "(tenant_id,operation,idempotency_key,status,request_hash) "
                + "values (1,'LEAD_CONVERT','idem-1','PROCESSING','abc')");
        SQLException duplicate = assertThrows(SQLException.class, () -> execute("insert into crm_idempotency_record "
                + "(tenant_id,operation,idempotency_key,status,request_hash) "
                + "values (1,'LEAD_CONVERT','idem-1','PROCESSING','abc')"));
        assertEquals("23505", duplicate.getSQLState());
        execute("update crm_idempotency_record set status='COMPLETED', completed_at=now(), "
                + "response_body='{}'::jsonb where tenant_id=1 and operation='LEAD_CONVERT' and idempotency_key='idem-1'");
    }

    private void verifyLeadConversion() throws Exception {
        long ownerId = Long.parseLong(scalar("select owner_user_id from crm_lead where id=2001"));
        inTransaction(connection -> {
            update(connection, "insert into crm_customer "
                    + "(id,tenant_id,customer_no,name,status,ownership_type,owner_user_id,version,created_by,updated_by) "
                    + "values (3001,1,'C-3001','转换客户','ACTIVE','PRIVATE',?,0,?,?)", ownerId, ownerId, ownerId);
            history(connection, 940001, 1, "CUSTOMER", 3001, "CREATE", ownerId, "convert-1");
            audit(connection, 940002, 1, ownerId, "CUSTOMER_CREATE", "CUSTOMER", 3001, "convert-1");
            outbox(connection, 940003, 1, "CUSTOMER", 3001, "CustomerCreated", "convert-1");

            assertEquals(1, update(connection, "update crm_lead set status='CONVERTED', customer_id=3001, "
                    + "version=version+1, updated_by=? where tenant_id=1 and id=2001 and version=1", ownerId));
            history(connection, 940004, 1, "LEAD", 2001, "CONVERT", ownerId, "convert-1");
            audit(connection, 940005, 1, ownerId, "LEAD_CONVERT", "LEAD", 2001, "convert-1");
            outbox(connection, 940006, 1, "LEAD", 2001, "LeadConverted", "convert-1");
        });
        assertEquals("3001", scalar("select customer_id from crm_lead where id=2001 and status='CONVERTED'"));

        assertThrows(SQLException.class, () -> execute("update crm_lead set ownership_type='PUBLIC', "
                + "owner_user_id=null, public_pool_id=1001, pool_entered_at=now() where id=2001"));
    }

    private void verifyCustomerMerge() throws Exception {
        insertCustomer(3101, 1, "C-3101", "合并源客户", 101);
        insertCustomer(3102, 1, "C-3102", "合并目标客户", 102);
        insertLead(2101, 1, "L-2101", "PRIVATE", 101L, null, "CONVERTED", 3101L);
        execute("insert into crm_contact (id,tenant_id,customer_id,name,created_by,updated_by) "
                + "values (4101,1,3101,'源联系人',101,101)");
        execute("insert into crm_follow_up (id,tenant_id,customer_id,actor_user_id,channel,content,created_by) "
                + "values (4201,1,3101,101,'PHONE','merge me',101)");

        inTransaction(connection -> {
            update(connection, "update crm_contact set source_customer_id=customer_id, customer_id=3102, "
                    + "updated_by=101, updated_at=now(), version=version+1 where tenant_id=1 and customer_id=3101");
            update(connection, "update crm_follow_up set customer_id=3102, updated_by=101, updated_at=now(), "
                    + "version=version+1 where tenant_id=1 and customer_id=3101");
            update(connection, "update crm_lead set customer_id=3102, updated_by=101, updated_at=now(), "
                    + "version=version+1 where tenant_id=1 and customer_id=3101");
            update(connection, "update crm_customer set merged_into_customer_id=3102, merged_at=now(), "
                    + "deleted_at=now(), deleted_by=101, updated_by=101, version=version+1 "
                    + "where tenant_id=1 and id=3101 and deleted_at is null");
            history(connection, 950001, 1, "CUSTOMER", 3101, "MERGE", 101, "merge-1");
            audit(connection, 950002, 1, 101, "CUSTOMER_MERGE", "CUSTOMER", 3101, "merge-1");
            outbox(connection, 950003, 1, "CUSTOMER", 3101, "CustomerMerged", "merge-1");
        });

        assertEquals("3102", scalar("select customer_id from crm_contact where id=4101"));
        assertEquals("3102", scalar("select customer_id from crm_follow_up where id=4201"));
        assertEquals("3102", scalar("select customer_id from crm_lead where id=2101"));
        assertEquals("1", scalar("select count(*) from crm_customer where id=3101 "
                + "and deleted_at is not null and merged_into_customer_id=3102"));
    }

    private void verifyHandover() throws Exception {
        insertLead(2201, 1, "L-2201", "PRIVATE", 101L, null, "NEW", null);
        inTransaction(connection -> {
            update(connection, "update crm_lead set owner_user_id=102, version=version+1, updated_by=101 "
                    + "where tenant_id=1 and id=2201 and owner_user_id=101 and version=0");
            history(connection, 960001, 1, "LEAD", 2201, "HANDOVER", 101, "handover-1");
            audit(connection, 960002, 1, 101, "LEAD_HANDOVER", "LEAD", 2201, "handover-1");
            outbox(connection, 960003, 1, "LEAD", 2201, "ResourcesHandedOver", "handover-1");
            update(connection, "insert into crm_resource_handover "
                    + "(id,tenant_id,from_user_id,to_user_id,resource_type,resource_id,status,created_by,"
                    + "completed_at,operation_id,batch_no) values "
                    + "(960004,1,101,102,'LEAD',2201,'COMPLETED',101,now(),'handover-1','batch-1')");
        });
        assertEquals("102", scalar("select owner_user_id from crm_lead where id=2201"));
        assertEquals("12", scalar("select owner_dept_id from crm_lead where id=2201"));
    }

    private void verifyImmutableHistoryAndRetryableOutbox() throws Exception {
        SQLException immutableHistory = assertThrows(SQLException.class,
                () -> execute("update crm_ownership_history set reason='tampered' where id=960001"));
        assertEquals("55000", immutableHistory.getSQLState());
        SQLException immutableAudit = assertThrows(SQLException.class,
                () -> execute("delete from crm_audit_log where id=960002"));
        assertEquals("55000", immutableAudit.getSQLState());

        execute("update crm_outbox_event set status='FAILED', retry_count=retry_count+1, "
                + "last_error='broker unavailable', next_retry_at=now()+interval '1 minute', updated_at=now() "
                + "where id=960003");
        assertEquals("FAILED:1", scalar("select status || ':' || retry_count from crm_outbox_event where id=960003"));
    }

    private void seedPlatform() throws Exception {
        execute("insert into crm_tenant (id,name) values (1,'tenant-a'),(2,'tenant-b')");
        execute("insert into crm_department (id,tenant_id,code,name,path) values "
                + "(11,1,'ROOT','根部门','/11'),(12,1,'SALES','销售部','/11/12'),"
                + "(21,2,'ROOT','根部门','/21')");
        execute("update crm_department set parent_id=11 where id=12");
        execute("insert into crm_user (id,tenant_id,department_id,username,name) values "
                + "(101,1,11,'u101','用户101'),(102,1,12,'u102','用户102'),(201,2,21,'u201','用户201')");
        execute("insert into crm_role (id,tenant_id,code,name,data_scope) values "
                + "(10001,1,'SELF','本人','SELF'),(10002,1,'DEPT','本部门','DEPARTMENT'),"
                + "(10003,1,'DEPT_SUB','本部门及下级','DEPARTMENT_AND_SUB'),(10004,1,'ALL','全部','ALL'),"
                + "(20001,2,'SELF','本人','SELF')");
        execute("insert into crm_public_pool "
                + "(id,tenant_id,resource_type,code,name,auto_recycle_enabled,recycle_after_days,created_by,updated_by) values "
                + "(1001,1,'LEAD','DEFAULT','线索公海',true,30,101,101),"
                + "(1002,1,'CUSTOMER','DEFAULT','客户公海',true,60,101,101),"
                + "(2001,2,'LEAD','DEFAULT','线索公海',true,30,201,201)");
    }

    private void insertLead(long id, long tenantId, String leadNo, String ownershipType,
                            Long ownerUserId, Long poolId, String status, Long customerId) throws SQLException {
        long actor = tenantId == 1 ? 101 : 201;
        inTransaction(connection -> {
            update(connection, "insert into crm_lead "
                    + "(id,tenant_id,lead_no,name,source_type,status,customer_id,ownership_type,owner_user_id,public_pool_id,"
                    + "pool_entered_at,created_by,updated_by) values (?,?,?,?, 'MANUAL',?,?,?,?,?,case when ?='PUBLIC' "
                    + "then now() else null end,?,?)", id, tenantId, leadNo, leadNo, status, customerId,
                    ownershipType, ownerUserId, poolId, ownershipType, actor, actor);
            history(connection, 8_000_000 + id, tenantId, "LEAD", id, "CREATE", actor, "create-lead-" + id);
            audit(connection, 8_100_000 + id, tenantId, actor, "LEAD_CREATE", "LEAD", id, "create-lead-" + id);
            outbox(connection, 8_200_000 + id, tenantId, "LEAD", id, "LeadCreated", "create-lead-" + id);
        });
    }

    private void insertCustomer(long id, long tenantId, String customerNo, String name, long ownerId)
            throws SQLException {
        inTransaction(connection -> {
            update(connection, "insert into crm_customer "
                    + "(id,tenant_id,customer_no,name,status,ownership_type,owner_user_id,created_by,updated_by) "
                    + "values (?,?,?,?,'ACTIVE','PRIVATE',?,?,?)", id, tenantId, customerNo, name, ownerId, ownerId, ownerId);
            history(connection, 8_300_000 + id, tenantId, "CUSTOMER", id, "CREATE", ownerId, "create-customer-" + id);
            audit(connection, 8_400_000 + id, tenantId, ownerId, "CUSTOMER_CREATE", "CUSTOMER", id,
                    "create-customer-" + id);
            outbox(connection, 8_500_000 + id, tenantId, "CUSTOMER", id, "CustomerCreated",
                    "create-customer-" + id);
        });
    }

    private void history(Connection connection, long id, long tenantId, String resourceType, long resourceId,
                         String action, long actor, String operationId) throws SQLException {
        update(connection, "insert into crm_ownership_history "
                + "(id,tenant_id,resource_type,resource_id,action,operator_user_id,operation_id) values (?,?,?,?,?,?,?)",
                id, tenantId, resourceType, resourceId, action, actor, operationId);
    }

    private void audit(Connection connection, long id, long tenantId, long actor, String action,
                       String resourceType, long resourceId, String operationId) throws SQLException {
        update(connection, "insert into crm_audit_log "
                + "(id,tenant_id,actor_user_id,action,resource_type,resource_id,before_data,after_data,trace_id,operation_id) "
                + "values (?,?,?,?,?,?,'{}','{}',?,?)", id, tenantId, actor, action, resourceType, resourceId,
                "trace-" + operationId, operationId);
    }

    private void outbox(Connection connection, long id, long tenantId, String aggregateType,
                        long aggregateId, String eventType, String operationId) throws SQLException {
        update(connection, "insert into crm_outbox_event "
                + "(id,tenant_id,aggregate_type,aggregate_id,event_type,payload,occurred_at,operation_id,trace_id) "
                + "values (?,?,?,?,?,'{}',now(),?,?)", id, tenantId, aggregateType, aggregateId,
                eventType, operationId, "trace-" + operationId);
    }

    private void resetAndMigrate() throws SQLException {
        clean();
        migrate();
    }

    private void clean() throws SQLException {
        Flyway.configure().dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .cleanDisabled(false).load().clean();
    }

    private void migrate() {
        Flyway.configure().dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration").load().migrate();
    }

    private void migrateTo(String target) {
        Flyway.configure().dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration").target(target).load().migrate();
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
    }

    private void execute(String sql) throws SQLException {
        try (Connection connection = connection(); Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private String scalar(String sql) throws SQLException {
        try (Connection connection = connection(); Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            assertTrue(resultSet.next());
            return resultSet.getString(1);
        }
    }

    private int update(Connection connection, String sql, Object... args) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int index = 0; index < args.length; index++) {
                statement.setObject(index + 1, args[index]);
            }
            return statement.executeUpdate();
        }
    }

    private void inTransaction(SqlWork work) throws SQLException {
        try (Connection connection = connection()) {
            connection.setAutoCommit(false);
            try {
                work.execute(connection);
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    @FunctionalInterface
    private interface SqlWork {
        void execute(Connection connection) throws SQLException;
    }
}
