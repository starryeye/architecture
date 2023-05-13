package dev.practice.pay;

import dev.practice.pay.account.application.port.out.LoadAccountPort;
import dev.practice.pay.account.domain.Account;
import dev.practice.pay.account.domain.Money;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SendMoneySystemTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    LoadAccountPort loadAccountPort;

    @Test
    @Sql("SendMoneySystemTest.sql")// @Transactional ?
    void sendMoney() {

        //given
        Account.AccountId sourceAccountId = new Account.AccountId(1L);
        Account.AccountId targetAccountId = new Account.AccountId(2L);

        Account initSourceAccount = loadAccountPort.loadAccount(sourceAccountId, LocalDateTime.now());
        Account initTargetAccount = loadAccountPort.loadAccount(targetAccountId, LocalDateTime.now());

        Money initSourceAccountBalance = initSourceAccount.calculateBalance();
        Money initTargetAccountBalance = initTargetAccount.calculateBalance();

        Money sendMoneyAmount = Money.of(500L);

        //when
        ResponseEntity<Boolean> response = whenSendMoney(sourceAccountId, targetAccountId, sendMoneyAmount);

        //then
        Account afterSourceAccount = loadAccountPort.loadAccount(sourceAccountId, LocalDateTime.now());
        Account afterTargetAccount = loadAccountPort.loadAccount(targetAccountId, LocalDateTime.now());

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(afterSourceAccount.calculateBalance().getAmount())
                .isEqualTo(Money.add(initSourceAccountBalance, sendMoneyAmount.negate()).getAmount());
        Assertions.assertThat(afterTargetAccount.calculateBalance().getAmount())
                .isEqualTo(Money.add(initTargetAccountBalance, sendMoneyAmount).getAmount());
    }

    private ResponseEntity<Boolean> whenSendMoney(
            Account.AccountId sourceAccountId,
            Account.AccountId targetAccountId,
            Money amount
    ) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<?> request = new HttpEntity<>(null, headers);

        return testRestTemplate.exchange(
                "/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}",
                HttpMethod.POST,
                request,
                Boolean.class,
                sourceAccountId.getValue(), targetAccountId.getValue(), amount.getAmount()
        );
    }
}
