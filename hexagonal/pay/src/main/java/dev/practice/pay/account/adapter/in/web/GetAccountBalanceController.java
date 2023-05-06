package dev.practice.pay.account.adapter.in.web;

import dev.practice.pay.account.application.port.in.GetAccountBalanceQuery;
import dev.practice.pay.account.domain.Account;
import dev.practice.pay.account.domain.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts}")
class GetAccountBalanceController {

    private final GetAccountBalanceQuery getAccountBalanceQuery;

    @GetMapping("/{accountId}/balance")
    AccountBalance getBalance(
            @PathVariable("accountId") Long accountId
    ) {
        Money accountBalance = getAccountBalanceQuery.getAccountBalance(
                new Account.AccountId(accountId)
        );

        return new AccountBalance(accountBalance.getAmount().longValue());
    }
}
