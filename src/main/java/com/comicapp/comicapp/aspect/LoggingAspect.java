package com.comicapp.comicapp.aspect;

import com.comicapp.comicapp.model.Comic;
import com.comicapp.comicapp.model.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * AOP untuk lapisan service (ORM/JPA).
 *
 * Aspect ini membungkus semua bean yang dianotasi @Service sehingga setiap
 * pemanggilan method service otomatis dicatat: saat masuk, saat berhasil,
 * dan saat gagal. Tidak ada satu pun method service yang perlu diubah.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    /** Semua method publik pada class yang dianotasi @Service. */
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceLayer() {
    }

    @Before("serviceLayer()")
    public void logMethodEntry(JoinPoint joinPoint) {
        log.info("[AOP] -> {}.{}() ({} argumen)",
                declaringType(joinPoint),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs().length);
    }

    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        log.info("[AOP] <- {}.{}() = {}",
                declaringType(joinPoint),
                joinPoint.getSignature().getName(),
                ringkasHasil(result));
    }

    @AfterThrowing(pointcut = "serviceLayer()", throwing = "exception")
    public void logMethodError(JoinPoint joinPoint, Throwable exception) {
        log.error("[AOP] !! {}.{}() gagal: {}",
                declaringType(joinPoint),
                joinPoint.getSignature().getName(),
                exception.getMessage());
    }

    private static String declaringType(JoinPoint joinPoint) {
        return joinPoint.getSignature().getDeclaringType().getSimpleName();
    }

    /**
     * Ringkasan hasil tanpa membocorkan data sensitif (password user).
     */
    private static String ringkasHasil(Object result) {
        if (result == null) {
            return "null";
        }
        if (result instanceof User user) {
            return "User{id=" + user.getId() + ", username='" + user.getUsername()
                    + "', role='" + user.getRole() + "'}";
        }
        if (result instanceof Comic comic) {
            return "Comic{id=" + comic.getId() + ", title='" + comic.getTitle() + "'}";
        }
        if (result instanceof Collection<?> collection) {
            return "Collection(size=" + collection.size() + ")";
        }
        return result.getClass().getSimpleName();
    }
}
