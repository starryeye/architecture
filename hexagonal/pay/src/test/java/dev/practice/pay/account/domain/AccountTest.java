package dev.practice.pay.account.domain;

import dev.practice.pay.common.AccountTestData;
import dev.practice.pay.common.ActivityTestData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void calculateBalance() {

        //given
        Account.AccountId accountId = new Account.AccountId(1L);

        Activity activity1 = ActivityTestData.defaultActivity()
                .withTargetAccount(accountId)
                .withMoney(Money.of(999L))
                .build();
        Activity activity2 = ActivityTestData.defaultActivity()
                .withTargetAccount(accountId)
                .withMoney(Money.of(1L))
                .build();
        Activity activity3 = ActivityTestData.defaultActivity()
                .withSourceAccount(accountId)
                .withMoney(Money.of(300L))
                .build();

        ActivityWindow activityWindow = new ActivityWindow(activity1, activity2, activity3);

        Account account = AccountTestData.defaultAccount()
                .withAccountId(accountId)
                .withBaselineBalance(Money.of(500L))
                .withActivityWindow(activityWindow)
                .build();

        //when
        Money balance = account.calculateBalance();

        //then
        Assertions.assertThat(balance).isEqualTo(Money.of(1200L));
    }

    @Test
    void withdrawSuccess() {
        //given
        Account.AccountId accountId = new Account.AccountId(1L);
        Account.AccountId targetAccountId = new Account.AccountId(101L);

        Activity activity1 = ActivityTestData.defaultActivity()
                .withTargetAccount(accountId)
                .withMoney(Money.of(999L))
                .build();
        Activity activity2 = ActivityTestData.defaultActivity()
                .withTargetAccount(accountId)
                .withMoney(Money.of(1L))
                .build();
        Activity activity3 = ActivityTestData.defaultActivity()
                .withSourceAccount(accountId)
                .withMoney(Money.of(300L))
                .build();

        ActivityWindow activityWindow = new ActivityWindow(activity1, activity2, activity3);

        Account account = AccountTestData.defaultAccount()
                .withAccountId(accountId)
                .withBaselineBalance(Money.of(500L))
                .withActivityWindow(activityWindow)
                .build();

        //when
        boolean result = account.withdraw(Money.of(1200L), targetAccountId);

        //then
        Assertions.assertThat(result).isTrue();
        Assertions.assertThat(account.getActivityWindow().getActivityList()).hasSize(4);
        Assertions.assertThat(account.calculateBalance()).isEqualTo(Money.of(0L));
    }

    @Test
    void withdrawFail() {
        //given
        Account.AccountId accountId = new Account.AccountId(1L);
        Account.AccountId targetAccountId = new Account.AccountId(101L);

        Activity activity1 = ActivityTestData.defaultActivity()
                .withTargetAccount(accountId)
                .withMoney(Money.of(999L))
                .build();
        Activity activity2 = ActivityTestData.defaultActivity()
                .withTargetAccount(accountId)
                .withMoney(Money.of(1L))
                .build();
        Activity activity3 = ActivityTestData.defaultActivity()
                .withSourceAccount(accountId)
                .withMoney(Money.of(300L))
                .build();

        ActivityWindow activityWindow = new ActivityWindow(activity1, activity2, activity3);

        Account account = AccountTestData.defaultAccount()
                .withAccountId(accountId)
                .withBaselineBalance(Money.of(500L))
                .withActivityWindow(activityWindow)
                .build();

        //when
        boolean result = account.withdraw(Money.of(1201L), targetAccountId);

        //then
        Assertions.assertThat(result).isFalse();
        Assertions.assertThat(account.getActivityWindow().getActivityList()).hasSize(3);
        Assertions.assertThat(account.calculateBalance()).isEqualTo(Money.of(1200L));
    }

    @Test
    void depositSuccess() {
        //given
        Account.AccountId accountId = new Account.AccountId(1L);
        Account.AccountId sourceAccountId = new Account.AccountId(101L);

        Activity activity1 = ActivityTestData.defaultActivity()
                .withTargetAccount(accountId)
                .withMoney(Money.of(999L))
                .build();
        Activity activity2 = ActivityTestData.defaultActivity()
                .withTargetAccount(accountId)
                .withMoney(Money.of(1L))
                .build();
        Activity activity3 = ActivityTestData.defaultActivity()
                .withSourceAccount(accountId)
                .withMoney(Money.of(300L))
                .build();

        ActivityWindow activityWindow = new ActivityWindow(activity1, activity2, activity3);

        Account account = AccountTestData.defaultAccount()
                .withAccountId(accountId)
                .withBaselineBalance(Money.of(500L))
                .withActivityWindow(activityWindow)
                .build();

        //when
        boolean result = account.deposit(Money.of(800L), sourceAccountId);

        //then
        Assertions.assertThat(result).isTrue();
        Assertions.assertThat(account.getActivityWindow().getActivityList()).hasSize(4);
        Assertions.assertThat(account.calculateBalance()).isEqualTo(Money.of(2000L));
    }
}