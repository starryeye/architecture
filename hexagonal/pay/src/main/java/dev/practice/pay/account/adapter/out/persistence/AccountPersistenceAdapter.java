package dev.practice.pay.account.adapter.out.persistence;

import dev.practice.pay.account.application.port.out.LoadAccountPort;
import dev.practice.pay.account.domain.Account;
import dev.practice.pay.account.domain.Activity;
import dev.practice.pay.account.domain.ActivityWindow;
import dev.practice.pay.account.domain.Money;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 양방향 매핑 전략
 */
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements LoadAccountPort {

    private final AccountRepository accountRepository;
    private final ActivityRepository activityRepository;

    /**
     * TODO : Query 수 줄여보기...
     */
    @Override
    public Account loadAccount(Account.AccountId accountId, LocalDateTime baselineDate) {


        AccountJpaEntity account = accountRepository.findById(accountId.getValue())
                .orElseThrow(EntityNotFoundException::new);

        List<ActivityJpaEntity> activities = activityRepository.getListByOwnerSince(account.getId(), baselineDate);

        Long depositBalance = orZero(activityRepository.getDepositBalanceUntil(account.getId(), baselineDate));
        Long withdrawalBalance = orZero(activityRepository.getWithdrawalBalanceUntil(account.getId(), baselineDate));

        //Mapping
        List<Activity> activityList = (List<Activity>) activities.stream()
                .map(activityJpaEntity -> new Activity(
                        new Activity.ActivityId(activityJpaEntity.getId()),
                        new Account.AccountId(activityJpaEntity.getOwnerAccountId()),
                        new Account.AccountId(activityJpaEntity.getSourceAccountId()),
                        new Account.AccountId(activityJpaEntity.getTargetAccountId()),
                        activityJpaEntity.getCreatedAt(),
                        Money.of(activityJpaEntity.getAmount())
                ));
        ActivityWindow activityWindow = new ActivityWindow(activityList);

        Money baselineBalance = Money.add(Money.of(depositBalance), Money.of(withdrawalBalance).negate());

        return Account.withId(
                new Account.AccountId(account.getId()),
                baselineBalance,
                activityWindow
        );
    }

    private Long orZero(Long value){
        return value == null ? 0L : value;
    }
}
