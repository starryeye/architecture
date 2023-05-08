package dev.practice.pay.account.application.service;

import dev.practice.pay.account.domain.Money;

public class ThresholdExceededException extends RuntimeException {

    public ThresholdExceededException(Money threshold, Money actual) {
        super(String.format("Maximum threshold for transferring money exceeded: tried to transfer %s but threshold is %s!", actual, threshold));
    }
}
