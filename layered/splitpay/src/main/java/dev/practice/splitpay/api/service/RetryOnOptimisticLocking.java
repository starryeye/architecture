package dev.practice.splitpay.api.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryOnOptimisticLocking {
    /**
     * TODO
     *
     * 서버 성능 스팩과 동일한 스팩이라도 성공할수도 실패할수도 있다.
     *
     * 다른 방법은 없는 건가...
     *
     * 1. sleep 을 없애고 무한 retry?
     * 2. 분산락..?
     */
    int value() default 20;
    long delay() default 100L;
}

