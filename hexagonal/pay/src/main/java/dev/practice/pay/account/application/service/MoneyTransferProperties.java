package dev.practice.pay.account.application.service;

import dev.practice.pay.account.domain.Money;
import lombok.*;

@Getter
@RequiredArgsConstructor
public class MoneyTransferProperties {

    private final Money maximumTransferThreshold;

}
