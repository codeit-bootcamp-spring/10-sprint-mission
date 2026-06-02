package com.sprint.mission.discodeit.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.auth.handler.DiscodeitAccessDeniedHandler;
import com.sprint.mission.discodeit.auth.handler.DiscodeitAuthenticationEntryPoint;
import com.sprint.mission.discodeit.auth.handler.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.auth.handler.JwtLogoutHandler;
import com.sprint.mission.discodeit.auth.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.auth.handler.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.auth.jwt.InMemoryJwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      JwtLoginSuccessHandler jwtLoginSuccessHandler,
      JwtLogoutHandler jwtlogoutHandler,
      LoginFailureHandler loginFailureHandler,
      DiscodeitAuthenticationEntryPoint authenticationEntryPoint,
      DiscodeitAccessDeniedHandler accessDeniedHandler,
      DiscodeitUserDetailsService discodeitUserDetailsService,
      JwtAuthenticationFilter jwtAuthenticationFilter
  ) throws Exception {

    http
        // SPA 환경을 위한 Cookie 기반 CSRF 및 Plain Token 검증 설정
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
            .ignoringRequestMatchers(toH2Console())
        )

        // 인가(Authorization) 규칙 설정
        .authorizeHttpRequests(auth -> auth
            // 내부 Forward 및 Error Dispatcher 허용
            .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()

            // 시스템 및 개발 도구 전용 엔드포인트
            .requestMatchers("/actuator/health", "/actuator/info").permitAll()
            .requestMatchers("/actuator/**").hasRole("ADMIN")
            .requestMatchers(toH2Console()).permitAll()

            // 인증/회원 관련 API
            .requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
            .requestMatchers(HttpMethod.POST,
                "/api/users",
                "/api/auth/login",
                "/api/auth/logout",
                "/api/auth/refresh"
            ).permitAll()

            // 정적 리소스 및 문서 뷰 (API 제외 모든 GET 요청)
            .requestMatchers(HttpMethod.GET, "/").permitAll()
            .requestMatchers(HttpMethod.GET, "/index.html", "/favicon.ico", "/assets/**").permitAll()

            .anyRequest().authenticated()
        )

        // 폼 로그인 인프라 구축 (성공/실패 핸들러는 API 응답형 커스텀 빈 사용)
        .formLogin(form -> form
            .loginProcessingUrl("/api/auth/login")
            .successHandler(jwtLoginSuccessHandler)
            .failureHandler(loginFailureHandler)
            .permitAll()
        )

        // 토큰 기반 자동 로그인(Remember-Me) 설정
        .rememberMe(remember -> remember
            .key("my-remember-key")
            .tokenValiditySeconds(7 * 24 * 60 * 60) // 7일 만료
            .rememberMeParameter("remember-me")
            .userDetailsService(discodeitUserDetailsService)
        )

        // 로그아웃 설정 (성공 시 204 No Content 반환)
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .addLogoutHandler(jwtlogoutHandler)
            .permitAll()
        )

        // API 커스텀 예외 처리 (401 Unauthorized / 403 Forbidden)
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        )

        // H2 콘솔 정상 작동을 위한 iframe 허용
        .headers(headers -> headers
            .frameOptions(FrameOptionsConfig::sameOrigin)
        )

        // 동시성 세션 제어 정책
        .sessionManagement(management -> management
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public JwtRegistry jwtRegistry(com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider jwtTokenProvider) {
    return new InMemoryJwtRegistry(jwtTokenProvider);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    // 기본 bcrypt 알고리즘 기반의 위임형 패스워드 인코더 반환
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy("""
        ROLE_ADMIN > ROLE_CHANNEL_MANAGER
        ROLE_CHANNEL_MANAGER > ROLE_USER
        """);
  }

  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }
}