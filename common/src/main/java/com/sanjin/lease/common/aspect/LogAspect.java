package com.sanjin.lease.common.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogAspect {
    private final LogService logService;

    // 1. 定义切入点：只要贴了 @Log 注解的方法都会被拦截
    @Pointcut("@annotation(com.sanjin.lease.common.aspect.Log))")
    public void logPointcut() {
    }

    // 2. 环绕通知：控制方法执行前后的逻辑
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // 1. 执行业务逻辑（主线程）
        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;

        // 2. 异步记录日志（非阻塞，主线程直接往下走）
        logService.saveLog(
                joinPoint.getSignature().getName(),
                duration,
                result,
                joinPoint.getArgs()
        );

        return result; // 3. 立即返回结果给用户
    }
}
