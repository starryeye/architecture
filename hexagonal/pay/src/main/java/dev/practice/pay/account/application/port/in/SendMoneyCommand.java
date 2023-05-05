package dev.practice.pay.account.application.port.in;

import dev.practice.pay.account.domain.Account;
import dev.practice.pay.account.domain.Money;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
public class SendMoneyCommand {

    @NonNull
    Account.AccountId sourceAccountId;

    @NonNull
    Account.AccountId targetAccountId;

    @NonNull
    Money money;

    @Builder
    public SendMoneyCommand(
            Account.AccountId sourceAccountId,
            Account.AccountId targetAccountId,
            Money money
    ) {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.money = money;
    }
}
