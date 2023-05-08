package dev.practice.pay.account.domain;

import lombok.NonNull;
import lombok.Value;

import java.math.BigInteger;

@Value
public class Money {

    public static Money ZERO = Money.of(0L);

    @NonNull
    BigInteger amount;

    public static Money of(long value) {
        return new Money(BigInteger.valueOf(value));
    }

    public static Money add(Money a, Money b) {
        return new Money(a.amount.add(b.amount));
    }

    public Money negate() {
        return new Money(this.amount.negate());
    }

    public boolean isGreaterThanOrEqualToZero() {
        return amount.compareTo(BigInteger.ZERO) >= 0;
    }

    public boolean isGreaterThan(Money money){
        return this.amount.compareTo(money.amount) == 1;
    }
}
