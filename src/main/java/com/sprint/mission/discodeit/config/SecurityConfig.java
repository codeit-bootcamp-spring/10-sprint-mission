package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;
  private final ObjectMapper objectMapper;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.csrf(
            csrf ->
                // CSRF는 세션이 아닌 쿠키에 저장
                // XSFT-TOKEN 값을 읽어 X-XSRF-TOKEN 헤더에 넣어야하므로, HttpOnly를 false로 설정
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
        .authorizeHttpRequests(
            auth ->
                auth
                    // CSRF 토큰 발급은 로그인 전에도 필요
                    .requestMatchers("/api/auth/csrf-token")
                    .permitAll()
                    // 회원가입은 로그인 전에
                    .requestMatchers(HttpMethod.POST, "/api/users")
                    .permitAll()
                    // 로그인 료청은 인증 전에 호출
                    .requestMatchers(HttpMethod.POST, "/api/auth/login")
                    .permitAll()
                    // 로그아웃 URL 자체는 Spring Security LogoutFilter가 처리
                    .requestMatchers(HttpMethod.POST, "/api/auth/logout")
                    .permitAll()
                    .requestMatchers(
                        "/",
                        "/index.html",
                        "/favicon.ico",
                        "/assets/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/actuator/**",
                        "/login")
                    .permitAll()
                    // 위에서 허용안한 것들은 인증 필.
                    .anyRequest()
                    .authenticated())

        // 인증/인가 실패 시 기본 redirect 대신 JSON 응답을 반환
        .exceptionHandling(
            ex ->
                ex
                    // 인증되지 않은 사용자가 보호된 API에 접근하면 401
                    .authenticationEntryPoint(
                        (request, response, authException) -> {
                          ErrorResponse errorResponse =
                              new ErrorResponse(
                                  Instant.now(),
                                  ErrorCode.INVALID_USER_CREDENTIALS.name(),
                                  "인증이 필요합니다.",
                                  Map.of("reason", authException.getMessage()),
                                  authException.getClass().getSimpleName(),
                                  HttpServletResponse.SC_UNAUTHORIZED);

                          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                          response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                          response.setCharacterEncoding("UTF-8");
                          objectMapper.writeValue(response.getWriter(), errorResponse);
                        })
                    // 인증은 되었지만 권한이 부족하면 403
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) -> {
                          ErrorResponse errorResponse =
                              new ErrorResponse(
                                  Instant.now(),
                                  "ACCESS_DENIED",
                                  "접근 권한이 없습니다.",
                                  Map.of("reason", accessDeniedException.getMessage()),
                                  accessDeniedException.getClass().getSimpleName(),
                                  HttpServletResponse.SC_FORBIDDEN);

                          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                          response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                          response.setCharacterEncoding("UTF-8");
                          objectMapper.writeValue(response.getWriter(), errorResponse);
                        }))

        // Post /api/auth/login 은 UsernamePasswordAuthenticatorFilter 가 처리.
        .formLogin(
            login ->
                login
                    .loginProcessingUrl("/api/auth/login")

                    // 로그인 시
                    .successHandler(loginSuccessHandler)
                    .failureHandler(loginFailureHandler))
        .logout(
            logout ->
                logout
                    .logoutUrl("/api/auth/logout")
                    // 로그아웃 성공 시 204 No Content
                    .logoutSuccessHandler(
                        new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)))
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy(
        """
      ROLE_ADMIN > ROLE_CHANNEL_MANAGER
      ROLE_CHANNEL_MANAGER > ROLE_USER
      """);
  }

  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }
}
