package dev.practice.pay.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;

import java.util.ArrayList;
import java.util.List;

public class ApplicationLayer extends ArchitectureElement{

    private final HexagonalArchitecture parentContext;

    private final List<String> incomingPortsPackages = new ArrayList<>();
    private final List<String> outgoingPortsPackages = new ArrayList<>();
    private final List<String> servicePackages = new ArrayList<>();

    ApplicationLayer(HexagonalArchitecture parentContext, String basePackage) {
        super(basePackage);
        this.parentContext = parentContext;
    }

    public ApplicationLayer addIncomingPorts(String packageName) {
        this.incomingPortsPackages.add(getFullQualifiedPackage(packageName));
        return this;
    }

    public ApplicationLayer addOutgoingPorts(String packageName) {
        this.outgoingPortsPackages.add(getFullQualifiedPackage(packageName));
        return this;
    }

    public ApplicationLayer addServices(String packageName) {
        this.servicePackages.add(getFullQualifiedPackage(packageName));
        return this;
    }

    public HexagonalArchitecture and() {
        return parentContext;
    }

    /**
     * application 은 파라미터의 패키지에 의존성이 없어야 한다.
     * 위반하면 테스트 실패
     */
    void doesNotDependOn(String packageName, JavaClasses classes) {
        denyDependency(this.getBasePackage(), packageName, classes);
    }

    /**
     * application 의 incoming port 와 outgoing port 는 서로 의존성 없어야 한다.
     * 위반하면 테스트 실패
     */
    void incomingAndOutgoingPortsDoNotDependOnEachOther(JavaClasses classes) {
        denyAnyDependency(incomingPortsPackages, outgoingPortsPackages, classes);
        denyAnyDependency(outgoingPortsPackages, incomingPortsPackages, classes);
    }

    private List<String> getAllPackages() {
        List<String> allPackages = new ArrayList<>();
        allPackages.addAll(incomingPortsPackages);
        allPackages.addAll(outgoingPortsPackages);
        allPackages.addAll(servicePackages);
        return allPackages;
    }

    void doesNotContainEmptyPackages() {
        denyEmptyPackages(getAllPackages());
    }
}
