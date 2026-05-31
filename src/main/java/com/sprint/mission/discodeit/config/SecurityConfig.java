package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.filter.JwtAuthenticationFilter;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

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

  // 세션 레지스트리
  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();

  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  // Security 필터 체인 등록
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      JwtLoginSuccessHandler jwtLoginSuccessfulHandler,
      LoginFailureHandler loginFailureHandler,
      SessionRegistry sessionRegistry,
      JwtAuthenticationFilter jwtAuthenticationFilter,
      JwtLogoutHandler jwtLogoutHandler)
      throws Exception {
    return http
        // csrf 관련 설정
        .csrf(csrf -> csrf
            .ignoringRequestMatchers(
                "/h2-console/**", // h2 콘솔로 들어가는 요청은 csrf 무시
                "/api/auth/login",
                "/api/auth/logout",
                "/api/auth/refresh")
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
            // 유저 조회
            .requestMatchers(HttpMethod.GET, "/api/users").authenticated()
            // 유저 수정
            .requestMatchers(HttpMethod.PATCH, "/api/users/**").authenticated()
            // 유저 삭제
            .requestMatchers(HttpMethod.DELETE, "/api/users/**").authenticated()
            // 메시지 관련 요청은 인증된 사용자만 접근 가능
            .requestMatchers("/api/messages/**").authenticated()
            // 읽기 정보 관련 요청은 인증된 사용자만 접근 가능
            .requestMatchers("/api/readStatuses/**").authenticated()
            // 공용 채널 생성은 어드민이나 채널 매니저만 요청 가능
            .requestMatchers(HttpMethod.POST, "/api/channels/public").hasRole("CHANNEL_MANAGER")
            .requestMatchers(HttpMethod.POST, "/api/channels/public").hasRole("ADMIN")
            // 채널 관련 요청은 인증된 사용자만 접근 가능
            .requestMatchers("/api/channels/**").authenticated()
            // 파일 관련 요청은 인증된 사용자만 접근 가능
            .requestMatchers("/api/binaryContents/**").authenticated()
            // refresh 토큰 발급 & 액세스 토큰 검증 요청은 누구나 접근 가능
            .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
            .requestMatchers("/files/**").authenticated()
            .anyRequest().denyAll()
        )
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint((request, response, authException) ->
                response.setStatus(HttpStatus.UNAUTHORIZED.value()))
            .accessDeniedHandler((request, response, accessDeniedException) ->
                response.setStatus(HttpStatus.FORBIDDEN.value()))
        )
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(jwtLoginSuccessfulHandler)
            .failureHandler(loginFailureHandler))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
            .addLogoutHandler(jwtLogoutHandler)
        )

        // 동시 세션 제어
        .sessionManagement(management -> management
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .build();
  }
}
