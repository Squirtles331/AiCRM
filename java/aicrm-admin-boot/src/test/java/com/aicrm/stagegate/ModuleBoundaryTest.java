package com.aicrm.stagegate;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ModuleBoundaryTest {
    @Test
    void controllersDoNotAccessMappersOrJdbcSalesAdapter() {
        JavaClasses classes = new ClassFileImporter().importPackages("com.aicrm");

        noClasses().that().haveSimpleNameEndingWith("Controller")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..mapper..", "com.aicrm.sales.infrastructure..")
                .check(classes);
    }
}
