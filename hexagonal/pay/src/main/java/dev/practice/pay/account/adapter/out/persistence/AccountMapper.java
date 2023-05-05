package dev.practice.pay.account.adapter.out.persistence;

import dev.practice.pay.account.domain.Account;
import dev.practice.pay.account.domain.Activity;
import dev.practice.pay.account.domain.ActivityWindow;
import dev.practice.pay.account.domain.Money;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class AccountMapper {

    Account mapToDomainEntity(
            AccountJpaEntity account,
            List<ActivityJpaEntity> activities,
            Long depositBalance,
            Long withdrawalBalance
    ) {
        ActivityWindow activityWindow = mapToActivityWindow(activities);

        Money baselineBalance = Money.add(Money.of(depositBalance), Money.of(withdrawalBalance).negate());

        return Account.withId(
                new Account.AccountId(account.getId()),
                baselineBalance,
                activityWindow
        );
    }

    ActivityWindow mapToActivityWindow(List<ActivityJpaEntity> activities) {

        List<Activity> activityList = (List<Activity>) activities.stream()
                .map(activityJpaEntity -> new Activity(
                        new Activity.ActivityId(activityJpaEntity.getId()),
                        new Account.AccountId(activityJpaEntity.getOwnerAccountId()),
                        new Account.AccountId(activityJpaEntity.getSourceAccountId()),
                        new Account.AccountId(activityJpaEntity.getTargetAccountId()),
                        activityJpaEntity.getCreatedAt(),
                        Money.of(activityJpaEntity.getAmount())
                ));
        return new ActivityWindow(activityList);
    }
}
