package dev.practice.pay.account.application.service;

import dev.practice.pay.account.application.port.in.SendMoneyCommand;
import dev.practice.pay.account.application.port.in.SendMoneyUseCase;
import dev.practice.pay.account.application.port.out.LoadAccountPort;
import dev.practice.pay.account.application.port.out.UpdateAccountStatePort;
import dev.practice.pay.account.domain.Account;
import dev.practice.pay.common.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@UseCase
@RequiredArgsConstructor
@Transactional
class SendMoneyService implements SendMoneyUseCase {

    private final LoadAccountPort loadAccountPort;
    private final UpdateAccountStatePort updateAccountStatePort;

    private final MoneyTransferProperties moneyTransferProperties;

    /**
     * TODO: 낙관적 락, index 를 ownerAccountId 로 잡자
     */
    @Override
    public boolean sendMoney(SendMoneyCommand command) {

        checkThreshold(command);

        LocalDateTime now = LocalDateTime.now();

        Account sourceAccount = loadAccountPort.loadAccount(
                command.getSourceAccountId(),
                now
        );

        Account targetAccount = loadAccountPort.loadAccount(
                command.getTargetAccountId(),
                now
        );

        if(!sourceAccount.withdraw(command.getMoney(), targetAccount.getAccountId().orElseThrow())) {
            return false;
        }

        if(!targetAccount.deposit(command.getMoney(), sourceAccount.getAccountId().orElseThrow())) {
            return false;
        }

        updateAccountStatePort.updateActivities(sourceAccount);
        updateAccountStatePort.updateActivities(targetAccount);

        return true;
    }

    private void checkThreshold(SendMoneyCommand command) {
        if(command.getMoney().isGreaterThan(moneyTransferProperties.getMaximumTransferThreshold())){
            throw new ThresholdExceededException(moneyTransferProperties.getMaximumTransferThreshold(), command.getMoney());
        }
    }
}
