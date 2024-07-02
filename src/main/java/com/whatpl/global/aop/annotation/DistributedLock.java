package com.whatpl.global.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Redisson 을 이용한 분산락 AOP 적용 애노테이션
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * 락 이름
     */
    String name();

    /**
     * 락 대기 시간
     * 락 획득을 위해 waitTime 만큼 대기한다.
     */
    long waitTime() default 5L;

    /**
     * 락 임대 시간
     * 락을 획득한 이후 leaseTime 이 지나면 락을 해제한다.
     */
    long leaseTime() default 3L;

    /**
     * 락 시간 단위
     */
    TimeUnit unit() default TimeUnit.SECONDS;
}
