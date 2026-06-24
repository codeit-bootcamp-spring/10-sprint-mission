package com.sprint.mission.discodeit.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

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

/**
 * 시스템 전반의 보안 정책(인증/인가)을 설정하는 설정 클래스입니다.
 */
@Configuration
@EnableMethodSecurity // @PreAuthorize 활성화
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
        // 1. CSRF 설정: SPA 환경에 맞춰 Cookie 기반 및 커스텀 요청 핸들러 사용
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
            .ignoringRequestMatchers(antMatcher("/h2-console/**"))
        )

        // 2. 인가 규칙: 정적 리소스 및 일부 공용 API는 전체 허용, 나머지는 인증 필수
        .authorizeHttpRequests(auth -> auth
            .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
            .requestMatchers("/actuator/**").permitAll()
            .requestMatchers("/ws/**").permitAll()
            .requestMatchers(antMatcher("/h2-console/**")).permitAll()
            .requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/binaryContents/*").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users", "/api/auth/login", "/api/auth/refresh").permitAll()
            .requestMatchers(HttpMethod.GET, "/", "/index.html", "/favicon.ico", "/assets/**").permitAll()
            .anyRequest().authenticated()
        )

        // 3. 로그인/로그아웃: API 기반 처리를 위해 커스텀 핸들러 연결
        .formLogin(form -> form
            .loginProcessingUrl("/api/auth/login")
            .successHandler(jwtLoginSuccessHandler)
            .failureHandler(loginFailureHandler)
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .addLogoutHandler(jwtlogoutHandler)
            .logoutSuccessHandler((request, response, authentication) -> {
                response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_NO_CONTENT);
            })
            .permitAll()
        )

        // 4. 예외 처리: 401(인증실패), 403(권한부족) 응답 커스터마이징
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        )

        // 5. 세션 정책: JWT 사용을 위해 무상태(STATELESS)로 설정
        .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // 6. 필터 순서: UsernamePasswordAuthenticationFilter 이전에 JWT 필터 배치
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

        .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin));

    return http.build();
  }

  @Bean
  public JwtRegistry jwtRegistry(com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider jwtTokenProvider) {
    return new InMemoryJwtRegistry(jwtTokenProvider);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  // 계층형 권한 설정 (ADMIN은 자동으로 모든 하위 권한을 가짐)
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