package dev.practice.pay.account.adapter.out.persistence;

import dev.practice.pay.account.domain.Account;
import dev.practice.pay.account.domain.ActivityWindow;
import dev.practice.pay.account.domain.Money;
import dev.practice.pay.common.AccountTestData;
import dev.practice.pay.common.ActivityTestData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest //스프링 컨테이너 + JPA 관련 설정
@Import({AccountPersistenceAdapter.class, AccountMapper.class}) //해당 클래스를 스프링 빈으로 등록
class AccountPersistenceAdapterTest {

    @Autowired
    private AccountPersistenceAdapter adapter;
    @Autowired
    private ActivityRepository activityRepository;

    @Test
    @Sql("AccountPersistenceAdapterTest.sql") //test data insert
    void loadAccount() {
        Account account = adapter.loadAccount(new Account.AccountId(1L), LocalDateTime.of(2022, 5, 10, 0, 0));

        Assertions.assertThat(account.getActivityWindow().getActivityList()).hasSize(2);
        Assertions.assertThat(account.calculateBalance()).isEqualTo(Money.of(500));
    }

    @Test
    void saveActivities() {
        Account account = AccountTestData.defaultAccount()
                .withBaselineBalance(Money.of(500L))
                .withActivityWindow(
                        new ActivityWindow(
                                ActivityTestData.defaultActivity()
                                        .withActivityId(null)
                                        .withMoney(Money.of(1L))
                                        .build()
                        )
                )
                .build();

        adapter.saveActivities(account);

        Assertions.assertThat(activityRepository.count()).isEqualTo(1);

        ActivityJpaEntity savedActivity = activityRepository.findAll().get(0);
        Assertions.assertThat(savedActivity.getAmount()).isEqualTo(1L);
    }
}