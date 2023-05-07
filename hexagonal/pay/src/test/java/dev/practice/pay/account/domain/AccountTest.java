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
}