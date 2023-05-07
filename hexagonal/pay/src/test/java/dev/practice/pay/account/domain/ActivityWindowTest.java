package dev.practice.pay.account.domain;

import dev.practice.pay.common.ActivityTestData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class ActivityWindowTest {
    
    @Test
    void getStartTime() {
        //given
        LocalDateTime startTime = LocalDateTime.now().minusDays(10);
        LocalDateTime betweenTime = LocalDateTime.now().minusDays(5);
        LocalDateTime endTime = LocalDateTime.now();

        Activity startActivity = ActivityTestData.defaultActivity()
                .withCreatedAt(startTime)
                .build();
        Activity betweenActivity = ActivityTestData.defaultActivity()
                .withCreatedAt(betweenTime)
                .build();
        Activity endActivity = ActivityTestData.defaultActivity()
                .withCreatedAt(endTime)
                .build();
        
        ActivityWindow window = new ActivityWindow(startActivity, betweenActivity, endActivity);

        //when
        LocalDateTime getStartTime = window.getStartTime();

        //then
        Assertions.assertThat(getStartTime).isEqualTo(startTime);
    }

    @Test
    void getEndTime() {
        //given
        LocalDateTime startTime = LocalDateTime.now().minusDays(10);
        LocalDateTime betweenTime = LocalDateTime.now().minusDays(5);
        LocalDateTime endTime = LocalDateTime.now();

        Activity startActivity = ActivityTestData.defaultActivity()
                .withCreatedAt(startTime)
                .build();
        Activity betweenActivity = ActivityTestData.defaultActivity()
                .withCreatedAt(betweenTime)
                .build();
        Activity endActivity = ActivityTestData.defaultActivity()
                .withCreatedAt(endTime)
                .build();

        ActivityWindow window = new ActivityWindow(startActivity, betweenActivity, endActivity);

        //when
        LocalDateTime getEndTime = window.getEndTime();

        //then
        Assertions.assertThat(getEndTime).isEqualTo(endTime);
    }

    @Test
    void calculateBalance() {
        //given
        Account.AccountId accountId1 = new Account.AccountId(1L);
        Account.AccountId accountId2 = new Account.AccountId(101L);

        Activity activity1 = ActivityTestData.defaultActivity()
                .withSourceAccount(accountId1)
                .withTargetAccount(accountId2)
                .withMoney(Money.of(999L))
                .build();
        Activity activity2 = ActivityTestData.defaultActivity()
                .withSourceAccount(accountId1)
                .withTargetAccount(accountId2)
                .withMoney(Money.of(1L))
                .build();
        Activity activity3 = ActivityTestData.defaultActivity()
                .withSourceAccount(accountId2)
                .withTargetAccount(accountId1)
                .withMoney(Money.of(200L))
                .build();

        ActivityWindow window = new ActivityWindow(activity1, activity2, activity3);

        //when
        Money balanceOfAccountId1 = window.calculateBalance(accountId1);
        Money balanceOfAccountId2 = window.calculateBalance(accountId2);

        //then
        Assertions.assertThat(balanceOfAccountId1).isEqualTo(Money.of(800L).negate());
        Assertions.assertThat(balanceOfAccountId2).isEqualTo(Money.of(800L));
    }
}
