package com.aicrm.sales.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class ModuleDependencyTest {
    private final JavaClasses classes = new ClassFileImporter().importPackages("com.aicrm");

    @Test
    void salesDomainDependsOnlyOnItselfAndSharedKernel() {
        classes().that().resideInAPackage("com.aicrm.sales.domain..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "java..", "com.aicrm.sales.domain..", "com.aicrm.kernel..")
                .check(classes);
    }
}
