package dev.practice.pay.account.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityRepository extends JpaRepository<ActivityJpaEntity, Long> {

    //List<ActivityJpaEntity> getAllByOwnerAccountIdAndCreatedAtAfter(Long accountId, LocalDateTime createdAt);
    @Query("""
           select a
           from ActivityJpaEntity a
           where a.ownerAccountId = :accountId and a.createdAt >= :createdAt
           """)
    List<ActivityJpaEntity> getListByOwnerSince(
            @Param("accountId") Long accountId,
            @Param("createdAt") LocalDateTime since
    );

    @Query("""
           select sum(a.amount)
           from ActivityJpaEntity a
           where a.ownerAccountId = :accountId 
           and a.sourceAccountId = :accountId 
           and a.createdAt < :createdAt
           """)
    Long getDepositBalanceUntil(
            @Param("accountId") Long accountId,
            @Param("createdAt") LocalDateTime until
    );

    @Query("""
           select sum(a.amount)
           from ActivityJpaEntity a
           where a.ownerAccountId = :accountId 
           and a.targetAccountId = :accountId 
           and a.createdAt < :createdAt
           """)
    Long getWithdrawalBalanceUntil(
            @Param("accountId") Long accountId,
            @Param("createdAt") LocalDateTime until
    );
}
