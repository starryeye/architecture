package dev.practice.pay.account.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDateTime;
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

    public Optional<AccountId> getAccountId() {
        return Optional.ofNullable(this.accountId);
    }

    /**
     * 계좌 총액 계산
     */
    public Money calculateBalance() {
        return Money.add(
                this.baselineBalance,
                this.activityWindow.calculateBalance(this.accountId)
        );
    }

    /**
     * 인출
     * - 잔액 검사 포함
     * TODO: 실패 처리 예외로 .. 검토
     */
    public boolean withdraw(Money money, AccountId targetAccountId) {

        if(!mayWithdraw(money)) {
            return false;
        }

        Activity withdrawal = new Activity(
                this.accountId,
                this.accountId,
                targetAccountId,
                LocalDateTime.now(),
                money
        );
        this.activityWindow.addActivity(withdrawal);
        return true;
    }

    private boolean mayWithdraw(Money money) {
        return Money.add(calculateBalance(), money.negate()).isGreaterThanOrEqualToZero();
    }

    /**
     * 예금
     */
    public boolean deposit(Money money, AccountId sourceAccountId) {
        Activity deposit = new Activity(
                this.accountId,
                sourceAccountId,
                this.accountId,
                LocalDateTime.now(),
                money
        );
        activityWindow.addActivity(deposit);
        return true;
    }

    //TODO : 꼭 class 안에 있어야 하는가?
    @Value
    public static class AccountId {
        Long value;
    }
}
