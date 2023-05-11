package dev.practice.pay;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

public class DependencyRuleTest {

    @Test
    void testPackageDependencies() {
        ArchRuleDefinition.noClasses()
                .that()
                .resideInAnyPackage("dev.practice.pay.account.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("dev.practice.pay.account.application..")
                .check(new ClassFileImporter().importPackages("dev.practice.pay.."));
    }
}
