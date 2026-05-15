package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

  // 권한 계층 구조
  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy("""
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

  // 비밀번호 인코더를 Bean으로 등록
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // Security 필터 체인 등록
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      LoginSuccessHandler loginSuccessfulHandler,
      LoginFailureHandler loginFailureHandler
  )
      throws Exception {
    return http
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/h2-console/**")
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )
        .headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions.sameOrigin())
        )
        .authorizeHttpRequests(auth -> auth
            // H2 console
            .requestMatchers("/h2-console/**").permitAll()

            // CSRF token 발급
            .requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
            .requestMatchers(HttpMethod.GET, "/csrf-token").permitAll()

            // 회원가입
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

            // 로그인
            .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

            // 로그아웃
            .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()

            // API가 아닌 요청: 정적 리소스, SPA, Swagger, Actuator
            .requestMatchers(
                "/",
                "/index.html",
                "/assets/**",
                "/static/**",
                "/favicon.ico",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/actuator/**"
            ).permitAll()

            // 권한 변경
            .requestMatchers(HttpMethod.PUT, "/api/auth/role").hasRole("ADMIN")
            .requestMatchers("/api/auth/me").authenticated()

            .anyRequest().permitAll()
        )
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint((request, response, authException) ->
                response.setStatus(HttpStatus.UNAUTHORIZED.value()))
            .accessDeniedHandler((request, response, accessDeniedException) ->
                response.setStatus(HttpStatus.FORBIDDEN.value()))
        )
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(loginSuccessfulHandler)
            .failureHandler(loginFailureHandler))
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
        )
        .build();
  }
}
