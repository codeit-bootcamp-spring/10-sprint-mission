package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.CustomAccessDeniedHandler;
import com.sprint.mission.discodeit.security.CustomAuthenticationEntryPoint;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final UserDetailsService userDetailsService;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // 권한 계층 정의
  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
    hierarchy.setHierarchy("ROLE_ADMIN > ROLE_CHANNEL_MANAGER\nROLE_CHANNEL_MANAGER > ROLE_USER");
    return hierarchy;
  }

  // 정의한 권한 체계를 메서드 시큐리티(AOP)에 주입
  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, SessionRegistry sessionRegistry)
      throws Exception {
    http
        // CSRF 보호 설정
        .csrf(csrf -> csrf
            // 프론트엔드(JS)에서 쿠키를 읽을 수 있도록 HttpOnly 설정을 false로 변경
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            // SPA 전용 핸들러 등록
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )
        // 세션 관리 설정
        .sessionManagement(management -> management
            .maximumSessions(1) // 동일한 계정으로 동시 로그인할 수 없도록 설정
            .maxSessionsPreventsLogin(false) // 기존 로그인 세션을 만료시킴 (기본값)
            .sessionRegistry(sessionRegistry) // 주입 받은 sessionRegistry 적용
        )
        // 인가 설정
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/", "/index.html",
                "/assets/**", "/favicon.ico")
            .permitAll() // 프론트엔드 정적 리소스

            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/auth/csrf-token")
            .permitAll() // Csrf Token 발급
            .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/users")
            .permitAll() // 회원가입
            .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/login")
            .permitAll() // 로그인
            .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/logout")
            .permitAll() // 로그아웃
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**",
                "/actuator/**").permitAll() // API가 아닌 요청(Swagger, Actuator)
            // 그 외의 모든 요청은 반드시 인증되어야 함
            .anyRequest().authenticated()
        )
        // 403 예외 핸들러 등록
        .exceptionHandling(ex -> ex
            // 권한 없음 (403)
            .accessDeniedHandler(customAccessDeniedHandler)
            // 인증 안 됨 (401)
            .authenticationEntryPoint(customAuthenticationEntryPoint)
        )
        // 로그인 설정
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login") // Security 필터가 낚아챌 로그인 URL
            .successHandler(loginSuccessHandler)   // 200 응답 핸들러
            .failureHandler(loginFailureHandler)   // 401 응답 핸들러
        )
        // 로그인 유지 설정 (RememberMe)
        .rememberMe(remember -> remember
            .rememberMeParameter("remember-me") // 프론트엔드에서 보낼 파라미터 이름 (체크박스 이름)
            .key("discodeit-super-secret-key")  // 쿠키 암호화에 사용할 고유 키
            .tokenValiditySeconds(3600 * 24 * 7) // 키 유효기간 (7일)
            .userDetailsService(userDetailsService) // 유저 정보 재조회용 서비스
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
