package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  private final LoginSuccessHandler loginSuccessHandler;
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, LoginSuccessHandler loginSuccessHandler) throws Exception {
    return http.csrf(
            csrf ->
                // CSRF는 세션이 아닌 쿠키에 저장
                // XSFT-TOKEN 값을 읽어 X-XSRF-TOKEN 헤더에 넣어야하므로, HttpOnly를 false로 설정
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))

        // formLogin 추가
        // Post /api/auth/login 은 UsernamePasswordAuthenticatorFilter 가 처리.
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            // 로그인 성공 시
            .successHandler(loginSuccessHandler)
        )

        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
