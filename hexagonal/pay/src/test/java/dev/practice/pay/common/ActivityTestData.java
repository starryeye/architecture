package dev.practice.pay.common;

import dev.practice.pay.account.domain.Account;
import dev.practice.pay.account.domain.Activity;
import dev.practice.pay.account.domain.Money;

import java.time.LocalDateTime;

public class ActivityTestData {

    public static ActivityBuilder defaultActivity(){
        return new ActivityBuilder()
                .withOwnerAccount(new Account.AccountId(42L))
                .withSourceAccount(new Account.AccountId(42L))
                .withTargetAccount(new Account.AccountId(41L))
                .withCreatedAt(LocalDateTime.now())
                .withMoney(Money.of(999L));
    }

    public static class ActivityBuilder {
        private Activity.ActivityId activityId;
        private Account.AccountId ownerAccountId;
        private Account.AccountId sourceAccountId;
        private Account.AccountId targetAccountId;
        private LocalDateTime createdAt;
        private Money money;

        public ActivityBuilder withActivityId(Activity.ActivityId activityId) {
            this.activityId = activityId;
            return this;
        }

        public ActivityBuilder withOwnerAccount(Account.AccountId accountId) {
            this.ownerAccountId = accountId;
            return this;
        }

        public ActivityBuilder withSourceAccount(Account.AccountId accountId) {
            this.sourceAccountId = accountId;
            return this;
        }

        public ActivityBuilder withTargetAccount(Account.AccountId accountId) {
            this.targetAccountId = accountId;
            return this;
        }

        public ActivityBuilder withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ActivityBuilder withMoney(Money money) {
            this.money = money;
            return this;
        }

        public Activity build() {
            return new Activity(
                    this.activityId,
                    this.ownerAccountId,
                    this.sourceAccountId,
                    this.targetAccountId,
                    this.createdAt,
                    this.money);
        }
    }
}
