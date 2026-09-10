package com.aicrm.stagegate;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchemaContractStaticTest {
    @Test
    void migrationsAndArchitectureDocumentsMatchFrozenScope() throws IOException {
        Path repository = Path.of("").toAbsolutePath();
        while (repository != null && (!Files.isDirectory(repository.resolve("docs"))
                || !Files.isDirectory(repository.resolve("java")))) {
            repository = repository.getParent();
        }
        assertTrue(repository != null, "cannot locate repository root");
        Path migrations = repository.resolve("java/aicrm-admin-boot/src/main/resources/db/migration");
        List<Path> files;
        try (var stream = Files.list(migrations)) {
            files = stream.filter(path -> path.getFileName().toString().matches("V([1-9]|[12][0-9]|21)__.*\\.sql"))
                    .sorted(java.util.Comparator.comparingInt(this::version)).toList();
        }
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21), files.stream()
                .map(this::version).toList());

        String sql = files.stream().map(this::read).reduce("", String::concat).toLowerCase(Locale.ROOT);
        for (String required : List.of(
                "data_scope", "crm_role_field_permission", "auto_recycle_enabled", "rule_version",
                "invalid_reason", "ownership_type", "pool_entered_at", "before_snapshot", "after_snapshot",
                "idempotency_key", "processing", "completed", "failed", "crm_outbox_event",
                "crm_inbox_record", "ck_crm_lead_ownership", "ck_crm_customer_ownership")) {
            assertTrue(sql.contains(required), () -> "missing frozen SQL contract: " + required);
        }
        for (String futureTable : List.of(
                "crm_delivery", "crm_payment", "crm_invoice", "crm_ticket")) {
            assertFalse(sql.contains("create table " + futureTable),
                    () -> "future context table created before its stage gate: " + futureTable);
        }
        String lifecycleMigration = read(migrations.resolve("V17__contract_order_lifecycle_closure.sql")).toLowerCase(Locale.ROOT);
        assertTrue(lifecycleMigration.contains("delete from crm_permission"), "V17 must remove the retired ERP callback permission");

        Path architecture = repository.resolve("docs/architecture");
        for (String document : List.of(
                "README.md", "data-dictionary.md", "database-conventions.md", "authorization.md",
                "state-machines.md", "api-events.md", "error-codes.md", "reliability.md", "adr.md",
                "module-boundaries.md", "future-contexts.md", "stage-gate.md", "security-nfr.md", "approval.md", "contract-order.md", "analytics.md", "connectors.md")) {
            assertTrue(Files.isRegularFile(architecture.resolve(document)),
                    () -> "missing architecture document: " + document);
        }
    }

    private String read(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private int version(Path path) {
        String filename = path.getFileName().toString();
        return Integer.parseInt(filename.substring(1, filename.indexOf("__")));
    }
}
