package com.manasa.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.manasa.controller..*(..)) || " +
              "execution(* com.manasa.service.impl..*(..)) || " +
              "execution(* com.manasa.scheduler..*(..))")
    public void applicationFlowPointcut() {
    }

    @Around("applicationFlowPointcut()")
    public Object logMethodExecution(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String className = proceedingJoinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = proceedingJoinPoint.getSignature().getName();

        log.info("==> Entering {}.{}()", className, methodName);
        long startTime = System.currentTimeMillis();

        Object result = proceedingJoinPoint.proceed();

        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("<== Exiting {}.{}() | Execution time: {} ms", className, methodName, elapsedTime);

        return result;
    }

    @AfterThrowing(pointcut = "applicationFlowPointcut()", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.error("!!! Exception in {}.{}() | Cause: {}", className, methodName, exception.getMessage());
    }
}