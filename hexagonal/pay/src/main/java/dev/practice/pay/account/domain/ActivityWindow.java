package dev.practice.pay.account.domain;

import java.util.List;

public class ActivityWindow {

    private List<Activity> activityList;

    public Money calculateBalance(Account.AccountId accountId) {
        Money withdrawalBalance = activityList.stream()
                .filter(a -> a.getSourceAccountId().equals(accountId))
                .map(Activity::getMoney)
                .reduce(Money.ZERO, Money::add);

        Money depositBalance = activityList.stream()
                .filter(a -> a.getTargetAccountId().equals(accountId))
                .map(Activity::getMoney)
                .reduce(Money.ZERO, Money::add);

        return Money.add(depositBalance, withdrawalBalance.negate());
    }
}
