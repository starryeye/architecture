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

    void doesNotDependOn(String packageName, JavaClasses classes) {
        denyDependency(this.getBasePackage(), packageName, classes);
    }

    void doesNotContainEmptyPackages() {
        denyEmptyPackages(getAllAdapterPackages());
    }
}
