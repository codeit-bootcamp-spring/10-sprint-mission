package com.sprint.mission.discodeit.global.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class ServiceLoggingAspect {

  @Around("@annotation(com.sprint.mission.discodeit.global.log.ServiceLogAround)")
  public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
    String methodName = joinPoint.getSignature().toShortString();
    String fullName = String.join(".", className, methodName);
    Object[] args = joinPoint.getArgs();

    log.info("[start] {} | args: {}", fullName, args);
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Object result;
    try {
      result = joinPoint.proceed();
    } catch (Exception e) {
      log.error("[error] {} | message: {}", fullName, e.getMessage());
      throw e;
    } finally {
      stopWatch.stop();
    }
    log.info("[end] {} | time: {}s | result: {}", fullName, stopWatch.getTotalTimeSeconds(),
        result);
    return result;
  }
}
