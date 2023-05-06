package dev.practice.pay.account.adapter.in.web;

import dev.practice.pay.account.application.port.in.SendMoneyCommand;
import dev.practice.pay.account.application.port.in.SendMoneyUseCase;
import dev.practice.pay.account.domain.Account;
import dev.practice.pay.account.domain.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts/send")
class SendMoneyController {

    private final SendMoneyUseCase sendMoneyUseCase;

    @PostMapping("/{sourceAccountId}/{targetAccountId}/{amount}")
    Boolean sendMoney(
            @PathVariable("sourceAccountId") Long sourceAccountId,
            @PathVariable("targetAccountId") Long targetAccountId,
            @PathVariable("amount") Long amount
    ) {
        SendMoneyCommand command = new SendMoneyCommand(
                new Account.AccountId(sourceAccountId),
                new Account.AccountId(targetAccountId),
                Money.of(amount)
        );

        return sendMoneyUseCase.sendMoney(command);
    }
}
