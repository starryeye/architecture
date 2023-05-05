package dev.practice.pay.account.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@RequiredArgsConstructor
public class Activity {

    ActivityId activityId;

    @NonNull
    Account.AccountId ownerAccountId;

    @NonNull
    Account.AccountId sourceAccountId;

    @NonNull
    Account.AccountId targetAccountId;

    @NonNull
    LocalDateTime createdAt;

    @NonNull
    Money money;
    
    public Activity(
            @NonNull Account.AccountId ownerAccountId,
            @NonNull Account.AccountId sourceAccountId,
            @NonNull Account.AccountId targetAccountId,
            @NonNull LocalDateTime createdAt,
            @NonNull Money money
    ) {
        this.activityId = null;
        this.ownerAccountId = ownerAccountId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.createdAt = createdAt;
        this.money = money;
    }

    @Value
    public static class ActivityId {
        Long value;
    }
}
