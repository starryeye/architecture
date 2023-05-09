package dev.practice.pay.account.adapter.out.persistence;

import dev.practice.pay.account.domain.Account;
import dev.practice.pay.account.domain.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest //스프링 컨테이너 + JPA 관련 설정
@Import({AccountPersistenceAdapter.class, AccountMapper.class}) //해당 클래스를 스프링 빈으로 등록
class AccountPersistenceAdapterTest {

    @Autowired
    private AccountPersistenceAdapter adapter;
    @Autowired
    private ActivityRepository repository;

    @Test
    @Sql("AccountPersistenceAdapterTest.sql")
    void loadAccount() {
        Account account = adapter.loadAccount(new Account.AccountId(1L), LocalDateTime.of(2022, 5, 10, 0, 0));

        assertThat(account.getActivityWindow().getActivityList()).hasSize(2);
        assertThat(account.calculateBalance()).isEqualTo(Money.of(500));
    }
}