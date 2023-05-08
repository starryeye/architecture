package dev.practice.pay;

import dev.practice.pay.account.application.service.MoneyTransferProperties;
import dev.practice.pay.account.domain.Money;
import dev.practice.pay.common.PayConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan({"dev.practice.pay.common"})
public class PayConfiguration {

    @Bean
    public MoneyTransferProperties moneyTransferProperties(PayConfigurationProperties properties) {
        return new MoneyTransferProperties(Money.of(properties.getTransferThreshold()));
    }
}
