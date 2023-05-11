package dev.practice.pay.archunit;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.conditions.ArchConditions;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import lombok.Getter;

import java.util.List;

/**
 * Architecture 규칙을 테스트
 */
abstract class ArchitectureElement {

    /**
     * 아키텍처 테스트를 수행할 Base package
     */
    private final String basePackage;

    ArchitectureElement(String basePackage) {
        this.basePackage = basePackage;
    }

    String getFullQualifiedPackage(String relativePackage) {
        return basePackage + "." + relativePackage;
    }

    String getBasePackage() {
        return basePackage;
    }

    /**
     * fromPackage 에서 toPackage 방향으로 의존성을 가지면 안된다.
     * 위반하면 테스트 실패
     */
    static void denyDependency(String fromPackageName, String toPackageName, JavaClasses classes) {
        ArchRuleDefinition.noClasses()
                .that()
                .resideInAnyPackage(machAllClassesInPackage(fromPackageName))
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(machAllClassesInPackage(toPackageName))
                .check(classes);
    }

    static String machAllClassesInPackage(String packageName) {
        return packageName + "..";
    }

    static void denyAnyDependency(List<String> fromPackages, List<String> toPackages, JavaClasses classes) {
        for (String fromPackage : fromPackages) {
            for (String toPackage : toPackages) {
                denyDependency(fromPackage, toPackage, classes);
            }
        }
    }

    /**
     * 해당 패키지는 적어도 하나 이상의 클래스 파일을 가져야한다.
     * 위반하면 테스트 실패
     */
    void denyEmptyPackage(String packageName) {
        ArchRuleDefinition.classes()
                .that()
                .resideInAPackage(machAllClassesInPackage(packageName))
                .should(ArchConditions.containNumberOfElements(DescribedPredicate.greaterThanOrEqualTo(1)))
                .check(classesInPackage(packageName));
    }

    private JavaClasses classesInPackage(String packageName) {
        return new ClassFileImporter().importPackages(packageName);
    }

    void denyEmptyPackages(List<String> packages) {
        for (String packageName : packages) {
            denyEmptyPackage(packageName);
        }
    }
}
