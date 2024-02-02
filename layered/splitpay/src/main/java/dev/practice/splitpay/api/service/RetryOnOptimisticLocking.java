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
     * Optimistic lock 은 동시성 문제에 대해 대규모 트래픽이 들어오는 환경이면 부적절한 해결법이다.
     * -> 여기서는 1/N 정산하기 요청 대상자의 수를 제한하지 않았지만, 동시성 문제에 대한 대규모 트래픽은 없을 것이라 봤다.
     *
     *
     * [참고]
     * Optimistic lock 에서 retry 는 busy-waiting 이다.
     * 즉,
     * retry 시간 간격이 좁아지면..
     * 응답성이 좋아지지만, DB 에 부하가 증가한다.
     *
     * retry 시간 간격이 늘어나면..
     * 응답성이 안좋아지지만, DB 에 부하가 감소한다.
     * 또한, 스레드가 대기하는 시간이 늘어나므로 CPU 자원 낭비로 이어진다.
     *
     * I/O 에서 busy-wait 문제를 해결하기 위해서는
     * 비동기로 해결해야하지만, JPA(Jdbc) 를 사용하므로 근본적인 해결책은 존재하지 않는다.
     *
     * 해결법
     * 1.
     * DB 구조를 완전히 변경한다.
     * 예를 들면 동시성 문제가 발생하지 않도록 정산 완료 집계 테이블을 별도로 만들고
     * insert 구문으로 OOO 이 어떤 정산 건에 대해 완료 하였다고 처리한다.
     * 이렇게 한다면, 집계 로직이 따로 존재해야하며 실시간 성은 떨어질 수 있지만 최종적 일관성을 제공해야한다.
     *
     * 2.
     * 그냥 동시성 문제가 발생하는 로직의 속도를 매우 높게 가져간다.
     * 예를 들면 redis 로 정산 완료 count 를 처리한다.
     * redis 를 사용함으로써 동시성 문제도 없앨 수 있음 (싱글 스레드)
     *
     * 3.
     * Redisson 의 분산락 (pubsub 기반) 을 사용하면
     * DB 가 분산 환경일때 대안이 될 수 있다.
     * lettuce 는 스핀락(busy-wait)을 사용하여 redis 에 큰 부하를 주는데
     * pubsub 기반이면 부하를 없앨 수 있다.
     * 이 경우.. 응답성이 좋아진 것이고, DB 에 부하가 없어진 케이스가 된다. (락 획득 과정 관점을 보면 비동기 IO 임)
     *
     */
    int value() default 20;
    long delay() default 100L;
}

