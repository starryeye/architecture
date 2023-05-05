package dev.practice.pay.account.application.service;

import dev.practice.pay.account.application.port.in.GetAccountBalanceQuery;
import dev.practice.pay.account.application.port.out.LoadAccountPort;
import dev.practice.pay.account.domain.Account;
import dev.practice.pay.account.domain.Money;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class GetAccountBalanceService implements GetAccountBalanceQuery {

    private final LoadAccountPort loadAccountPort;

    @Override
    public Money getAccountBalance(Account.AccountId accountId) {

        LocalDateTime now = LocalDateTime.now();

        Account account = loadAccountPort.loadAccount(accountId, now);

        return account.calculateBalance();
    }
}
