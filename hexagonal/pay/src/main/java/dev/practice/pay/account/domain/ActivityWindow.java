package dev.practice.pay.account.domain;

import lombok.NonNull;

import java.util.Collections;
import java.util.List;

public class ActivityWindow {

    private List<Activity> activityList;

    public ActivityWindow(@NonNull List<Activity> activityList) {
        this.activityList = activityList;
    }

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

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public List<Activity> getActivityList() {
        return Collections.unmodifiableList(activityList);
    }
}
