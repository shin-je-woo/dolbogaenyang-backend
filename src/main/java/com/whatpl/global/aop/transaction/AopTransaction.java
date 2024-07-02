package com.whatpl.global.aop.transaction;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * AOP 적용 시 트랜잭션 분리를 위한 클래스
 */
@Component
public class AopTransaction {

    /**
     * 물리 트랜잭션을 분리
     * 트랜잭션 어드바이스와 커스텀 어드바이스 순서에 상관 없이 커스텀 어드바이스에서 트랜잭션이 수행됨을 보장
     * 커스텀 어드바이스에서 finally 구문 수행 시 필요 (ex. 락 해제)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
