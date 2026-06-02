package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.logging.LoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final MDCLoggingInterceptor mdcLoggingInterceptor;
    private final LoggingInterceptor loggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(mdcLoggingInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/css/**", "/js/**", "/error", "/h2-console/**", "/favicon.ico", "/swagger-ui/**", "/v3/api-docs/**");

        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/css/**", "/js/**", "/error", "/h2-console/**", "/favicon.ico", "/swagger-ui/**", "/v3/api-docs/**");
    }
}
