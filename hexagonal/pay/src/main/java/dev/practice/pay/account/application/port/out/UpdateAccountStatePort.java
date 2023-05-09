package dev.practice.pay.account.application.port.out;

import dev.practice.pay.account.domain.Account;

/**
 * 양방향 매핑
 */
public interface UpdateAccountStatePort {

    void saveActivities(Account account);
}
