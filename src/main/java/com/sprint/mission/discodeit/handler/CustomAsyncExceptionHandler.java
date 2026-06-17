package com.sprint.mission.discodeit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Component
@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("[Async 예외 발생] method={}, type={}, message={}, params={}",
                method.getName(), ex.getClass().getSimpleName(), ex.getMessage(), Arrays.toString(params));
    }
}
