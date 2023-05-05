package dev.practice.pay.account.application.port.in;

import dev.practice.pay.account.domain.Account;
import dev.practice.pay.account.domain.Money;

/**
 * 양방향 매핑 전략
 */
public interface GetAccountBalanceQuery {

    Money getAccountBalance(Account.AccountId accountId);
}
