package dev.practice.pay.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HexagonalArchitecture extends ArchitectureElement{

    private Adapters adapters;
    private ApplicationLayer applicationLayer;
    private List<String> domainPackages = new ArrayList<>();
    private String configurationPackage;

    public HexagonalArchitecture(String basePackage) {
        super(basePackage);
    }

    public static HexagonalArchitecture boundedContext(String basePackage) {
        return new HexagonalArchitecture(basePackage);
    }

    public Adapters withAdaptersLayer(String adaptersPackage) {
        this.adapters = new Adapters(this, getFullQualifiedPackage(adaptersPackage));
        return this.adapters;
    }

    public ApplicationLayer withApplicationLayer(String applicationPackage) {
        this.applicationLayer = new ApplicationLayer(this, getFullQualifiedPackage(applicationPackage));
        return this.applicationLayer;
    }

    public HexagonalArchitecture withDomainLayer(String domainPackage) {
        this.domainPackages.add(getFullQualifiedPackage(domainPackage));
        return this;
    }

    public HexagonalArchitecture withConfiguration(String packageName) {
        this.configurationPackage = getFullQualifiedPackage(packageName);
        return this;
    }

    /**
     * Domain 은 다른 패키지에 의존성이 있으면 안된다.
     * 위반시 테스트 실패
     */
    private void domainDoesNotDependOnOtherPackages(JavaClasses classes) {
        denyAnyDependency(
                this.domainPackages, Collections.singletonList(adapters.getBasePackage()), classes);
        denyAnyDependency(
                this.domainPackages, Collections.singletonList(applicationLayer.getBasePackage()), classes);
    }

    /**
     * 전체 의존성 테스트
     */
    public void check(JavaClasses classes) {

        this.adapters.doesNotContainEmptyPackages();
        this.adapters.dontDependOnEachOther(classes);
        this.adapters.doesNotDependOn(this.configurationPackage, classes);

        this.applicationLayer.doesNotContainEmptyPackages();
        this.applicationLayer.doesNotDependOn(this.adapters.getBasePackage(), classes);
        this.applicationLayer.doesNotDependOn(this.configurationPackage, classes);
        this.applicationLayer.incomingAndOutgoingPortsDoNotDependOnEachOther(classes);

        this.domainDoesNotDependOnOtherPackages(classes);
    }
}
