package com.comicapp.comicapp.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * AOP untuk mengukur durasi eksekusi method service.
 *
 * Dipakai untuk mendeteksi query ORM yang lambat (misalnya saat data komik
 * atau transaksi sudah banyak). Method service tetap sama persis; pengukuran
 * dilakukan secara transparan oleh proxy Spring AOP.
 */
@Aspect
@Component
public class PerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceAspect.class);

    /** Batas durasi (ms) yang dianggap lambat. */
    private static final long SLOW_THRESHOLD_MS = 1000L;

    @Around("execution(* com.comicapp.comicapp.service..*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            String method = joinPoint.getSignature().getDeclaringType().getSimpleName()
                    + "." + joinPoint.getSignature().getName() + "()";
            if (duration >= SLOW_THRESHOLD_MS) {
                log.warn("[AOP] {} lambat: {} ms", method, duration);
            } else {
                log.debug("[AOP] {} selesai dalam {} ms", method, duration);
            }
        }
    }
}
