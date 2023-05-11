package dev.practice.pay.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;

import java.util.ArrayList;
import java.util.List;

public class Adapters extends ArchitectureElement{

    private final HexagonalArchitecture parentContext;

    private final List<String> incomingAdapterPackages = new ArrayList<>();
    private final List<String> outgoingAdapterPackages = new ArrayList<>();

    Adapters(HexagonalArchitecture parentContext, String basePackage) {
        super(basePackage);
        this.parentContext = parentContext;
    }

    public Adapters addIncomingPackage(String packageName) {
        this.incomingAdapterPackages.add(getFullQualifiedPackage(packageName));
        return this;
    }

    public Adapters addOutgoingPackage(String packageName) {
        this.outgoingAdapterPackages.add(getFullQualifiedPackage(packageName));
        return this;
    }

    List<String> getAllAdapterPackages() {
        List<String> allAdapter = new ArrayList<>();
        allAdapter.addAll(incomingAdapterPackages);
        allAdapter.addAll(outgoingAdapterPackages);
        return allAdapter;
    }

    public HexagonalArchitecture and() {
        return parentContext;
    }

    /**
     * adapter 끼리는 서로 의존성이 없어야 한다.
     * 위반하면 테스트 실패
     */
    void dontDependOnEachOther(JavaClasses classes) {
        List<String> allAdapterPackages = getAllAdapterPackages();
        for (String allAdapterPackage1 : allAdapterPackages) {
            for (String allAdapterPackage2 : allAdapterPackages) {
                if(!allAdapterPackage1.equals(allAdapterPackage2)) {
                    denyDependency(allAdapterPackage1, allAdapterPackage2, classes);
                }
            }
        }
    }

    /**
     * adapter 는 파라미터의 패키지에 의존성이 없어야 한다.
     * 위반하면 테스트 실패
     */
    void doesNotDependOn(String packageName, JavaClasses classes) {
        denyDependency(this.getBasePackage(), packageName, classes);
    }

    /**
     * adapter 는 빈 패키지가 없어야 한다.
     * 위반하면 테스트 실패
     */
    void doesNotContainEmptyPackages() {
        denyEmptyPackages(getAllAdapterPackages());
    }
}
