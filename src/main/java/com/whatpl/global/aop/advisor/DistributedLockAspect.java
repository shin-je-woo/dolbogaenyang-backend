package com.whatpl.global.aop.advisor;

import com.whatpl.global.aop.annotation.DistributedLock;
import com.whatpl.global.aop.transaction.AopTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.util.CastUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private static final String REDISSON_LOCK_PREFIX = "lock:";
    private final RedissonClient redissonClient;
    private final AopTransaction aopTransaction;

    /**
     * aopTransaction.proceed(joinPoint) - 트랜잭션이 수행된 이후 락 해제가 보장된다.
     */
    @Around("@annotation(com.whatpl.global.aop.annotation.DistributedLock)")
    public Object execute(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = CastUtils.cast(joinPoint.getSignature());
        DistributedLock distributedLock = signature.getMethod().getAnnotation(DistributedLock.class);

        String name = REDISSON_LOCK_PREFIX + getSpELValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.name());
        RLock lock = redissonClient.getLock(name);

        try {
            boolean available = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.unit());
            if (!available) {
                log.info("DistributedLock 획득 실패. name = {}", name);
                return false;
            }

            return aopTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("DistributedLock 작업 쓰레드가 인터럽트 되었습니다. name = " + name, e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                } catch (IllegalMonitorStateException e) {
                    log.error("DistributedLock 이미 해제 되었습니다. name = {}", name);
                }
            }
        }
    }

    private Object getSpELValue(String[] parameterNames, Object[] args, String key) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return parser.parseExpression(key).getValue(context, Object.class);
    }
}
