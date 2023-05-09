package dev.practice.pay.account.adapter.out.persistence;

import dev.practice.pay.account.application.port.out.LoadAccountPort;
import dev.practice.pay.account.application.port.out.UpdateAccountStatePort;
import dev.practice.pay.account.domain.Account;
import dev.practice.pay.account.domain.Activity;
import dev.practice.pay.account.domain.ActivityWindow;
import dev.practice.pay.common.PersistenceAdapter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@PersistenceAdapter
@RequiredArgsConstructor
class AccountPersistenceAdapter implements LoadAccountPort, UpdateAccountStatePort {

    private final AccountRepository accountRepository;
    private final ActivityRepository activityRepository;

    private final AccountMapper accountMapper;

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

        return accountMapper.mapToDomainEntity(account, activities, depositBalance, withdrawalBalance);
    }

    private Long orZero(Long value){
        return value == null ? 0L : value;
    }

    @Override
    public void saveActivities(Account account) {

        List<Activity> activityList = account.getActivityWindow().getActivityList();
        for (Activity activity : activityList) {
            if(activity.getActivityId() == null) {
                activityRepository.save(accountMapper.mapToActivityJpaEntity(activity));
            }
        }
    }
}
