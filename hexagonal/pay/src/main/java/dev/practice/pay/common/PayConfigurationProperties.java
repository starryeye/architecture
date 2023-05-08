package dev.practice.pay.common;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@ConfigurationProperties(prefix = "pay")
@RequiredArgsConstructor
@Validated
public class PayConfigurationProperties {

    @Min(1)
    private final long transferThreshold;
}
