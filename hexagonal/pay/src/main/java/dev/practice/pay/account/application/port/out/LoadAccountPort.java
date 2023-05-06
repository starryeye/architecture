package dev.practice.pay.account.application.port.out;

import dev.practice.pay.account.domain.Account;

import java.time.LocalDateTime;

/**
 * 양방향 매핑 전략
 */
public interface LoadAccountPort {

    Account loadAccount(Account.AccountId accountId, LocalDateTime baselineDate);
}
