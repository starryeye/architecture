package dev.practice.pay.account.domain;

import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.*;

public class ActivityWindow {

    private List<Activity> activityList;

    public ActivityWindow(@NonNull List<Activity> activityList) {
        this.activityList = activityList;
    }

    public ActivityWindow(@NonNull Activity... activities) {
        this.activityList = new ArrayList<>(Arrays.asList(activities));
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

    public LocalDateTime getStartTime() {
        return activityList.stream()
                .min(Comparator.comparing(Activity::getCreatedAt))
                .orElseThrow(IllegalStateException::new)
                .getCreatedAt();
    }

    public LocalDateTime getEndTime() {
        return activityList.stream()
                .max(Comparator.comparing(Activity::getCreatedAt))
                .orElseThrow(IllegalStateException::new)
                .getCreatedAt();
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public List<Activity> getActivityList() {
        return Collections.unmodifiableList(activityList);
    }
}
