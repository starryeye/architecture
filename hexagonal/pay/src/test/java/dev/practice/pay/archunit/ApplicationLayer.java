package dev.practice.pay.archunit;

public class ApplicationLayer extends ArchitectureElement{

    private final HexagonalArchitecture parentContext;

    ApplicationLayer(HexagonalArchitecture parentContext, String basePackage) {
        super(basePackage);
        this.parentContext = parentContext;
    }

}
