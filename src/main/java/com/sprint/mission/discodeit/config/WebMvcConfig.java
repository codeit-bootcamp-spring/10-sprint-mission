package com.sprint.mission.discodeit.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

//  private final MDCLoggingInterceptor mdcLoggingInterceptor;
//
//  @Override
//  public void addInterceptors(InterceptorRegistry registry) {
//    registry.addInterceptor(mdcLoggingInterceptor)
//        .addPathPatterns("/**") // 모든 요청에 대해
//        .excludePathPatterns(//제외할 목록
//            "/h2-console/**",    // DB 콘솔
//            "/favicon.ico",      // 브라우저 아이콘
//            "/error",            // 스프링 기본 에러 페이지 (중복 로그 방지)
//            "/swagger-ui/**",    // Swagger UI
//            "/v3/api-docs/**",   // OpenAPI Docs
//            "/actuator/**"       // 모니터링용 (Admin 서버 요청)
//        );
//  }
}