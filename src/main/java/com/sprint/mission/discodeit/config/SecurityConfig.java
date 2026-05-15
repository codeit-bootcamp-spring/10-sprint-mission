package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // CSRF 보호 설정
        .csrf(csrf -> csrf
            // 프론트엔드(JS)에서 쿠키를 읽을 수 있도록 HttpOnly 설정을 false로 변경
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            // SPA 전용 핸들러 등록
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )
        // 인가 설정
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()
        )
        // 로그인 설정
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login") // Security 필터가 낚아챌 로그인 URL
            .successHandler(loginSuccessHandler)   // 200 응답 핸들러
            .failureHandler(loginFailureHandler)   // 401 응답 핸들러
        )
        // 로그아웃 설정
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout") // Security 필터가 낚아챌 로그아웃 URL
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(
                HttpStatus.NO_CONTENT)) // 302 리다이렉트 대신 204 응답 반환
        );
    return http.build();
  }
}
