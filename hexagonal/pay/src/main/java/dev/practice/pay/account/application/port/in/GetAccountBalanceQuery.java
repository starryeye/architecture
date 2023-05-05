package dev.practice.pay.account.application.port.in;

import dev.practice.pay.account.domain.Account;
import dev.practice.pay.account.domain.Money;

public interface GetAccountBalanceQuery {

    Money getAccountBalance(Account.AccountId accountId);
}
