package com.sprint.mission.discodeit.config.web;

import com.sprint.mission.discodeit.config.web.interceptor.MDCLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
// Interceptor 등록하는 설정 클래스
public class WebMvcConfig implements WebMvcConfigurer {

    private final MDCLoggingInterceptor mdcLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(mdcLoggingInterceptor);
    }
}
