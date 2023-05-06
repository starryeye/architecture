package dev.practice.pay.account.application.service;

import dev.practice.pay.account.application.port.in.SendMoneyCommand;
import dev.practice.pay.account.application.port.in.SendMoneyUseCase;
import dev.practice.pay.common.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
class SendMoneyService implements SendMoneyUseCase {
    @Override
    public boolean sendMoney(SendMoneyCommand command) {
        return false;
    }
}
