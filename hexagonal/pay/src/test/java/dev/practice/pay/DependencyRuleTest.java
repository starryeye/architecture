package dev.practice.pay;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import dev.practice.pay.archunit.HexagonalArchitecture;
import org.junit.jupiter.api.Test;

class DependencyRuleTest {

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

    @Test
    void validateRegistrationContextArchitecture() {

        HexagonalArchitecture.boundedContext("dev.practice.pay.account")

                .withDomainLayer("domain")

                .withAdaptersLayer("adapter")
                .addIncomingPackage("in.web")
                .addOutgoingPackage("out.persistence")
                .and()

                .withApplicationLayer("application")
                .addIncomingPorts("port.in")
                .addOutgoingPorts("port.out")
                .addServices("service")
                .and()

                .withConfiguration("configuration")

                .check(new ClassFileImporter().importPackages("dev.practice.pay.."));
    }
}
