package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      LoginSuccessHandler loginSuccessHandler,
      LoginFailureHandler loginFailureHandler,
      SessionRegistry sessionRegistry,
      DiscodeitUserDetailsService discodeitUserDetailsService
  ) throws Exception {
    http
        .cors(Customizer.withDefaults()
        )
        // csrf 보호 기능을 활성화
        // 토큰 저장소로 CookieCsrfTokenRepository를 사용
        .csrf(csrf -> csrf
            // XSRF-TOKEN 쿠키를 JavaScript에서 읽을 수 있도록 HttpOnly=false 설정
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )
        // POST /api/auth/login 요청을 Spring Security의 로그인 처리 URL로 지정
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )

        // rememberMe 설정
        .rememberMe(remember -> remember
            .rememberMeParameter("remember-me")
            .key("discodeit-remember-me-key")
            .tokenValiditySeconds(60 * 60 * 24 * 14)
            .userDetailsService(discodeitUserDetailsService)
        )

        // 로그아웃 성공 시 204 반환
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)
            )
        )
        // 동시 로그인 제한
        .sessionManagement(management -> management
            .sessionConcurrency(concurrency -> concurrency
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(sessionRegistry)
            )
        )
        // 예외 반환 (인증 안됨 401 / 권한 없음 403)
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) -> {
              response.setStatus(HttpStatus.UNAUTHORIZED.value());
            })
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              response.setStatus(HttpStatus.FORBIDDEN.value());
            })
        )
        .authorizeHttpRequests(auth -> auth
            // api가 아닌 요청은 인증없이 가능
            .requestMatchers(
                "/",
                "/index.html",
                "/assets/**",
                "/favicon.ico",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/actuator/**").permitAll()
            // CSRF 토큰 발행은 인증없이 가능해야 함
            .requestMatchers("/api/auth/csrf-token").permitAll()
            // 로그인은 인증없이 진행할 수 있어야 함
            .requestMatchers("/api/auth/login").permitAll()
            // 로그아웃 인증없이 진행
            .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
            // 회원가입은 인증없이 진행 가능해야함
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            // ADMIN권한 사용자만 진행가능
            .requestMatchers(HttpMethod.PUT, "/api/auth/role").hasRole("ADMIN")
            // 인증된 사용자만 진행 가능
            .requestMatchers("/api/auth/me").authenticated()
            // 그 외 모든 요청 인증 필요
            .anyRequest().authenticated()
        );
    return http.build();
  }

  // localhost:3000 프론트엔드에서 백엔드 API를 호출할 수 있도록 CORS 설정
  // CSRF 토큰을 쿠키와 X-XSRF-TOKEN 헤더로 주고받기 위해 credentials와 X-XSRF-TOKEN 헤더를 허용
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowedOrigins(List.of("http://localhost:3000"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Content-Type", "X-XSRF-TOKEN"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy("""
        ROLE_ADMIN > ROLE_CHANNEL_MANAGER
        ROLE_CHANNEL_MANAGER > ROLE_USER
        """);
  }

  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy
  ) {
    DefaultMethodSecurityExpressionHandler handler =
        new DefaultMethodSecurityExpressionHandler();

    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }
}
