package dev.practice.pay.account.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.util.Optional;

/**
 * Aggregate Root
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {

    /**
     * 계좌 ID
     */
    private final AccountId accountId;

    /**
     * 최초 계좌 금액
     */
    private final Money baselineBalance;

    /**
     * 거래 내역
     */
    private final ActivityWindow activityWindow;

    public static Account withoutId(
            Money baselineBalance,
            ActivityWindow activityWindow
    ) {
        return new Account(null, baselineBalance, activityWindow);
    }

    public static Account withId(
            AccountId accountId,
            Money baselineBalance,
            ActivityWindow activityWindow
    ) {
        return new Account(accountId, baselineBalance, activityWindow);
    }

    public Optional<AccountId> getId() {
        return Optional.ofNullable(this.accountId);
    }

    public Money calculateBalance() {
        return Money.add(
                this.baselineBalance,
                this.activityWindow.calculateBalance(this.accountId)
        );
    }

    //TODO : 꼭 class 안에 있어야 하는가?
    @Value
    public static class AccountId {
        Long value;
    }
}
