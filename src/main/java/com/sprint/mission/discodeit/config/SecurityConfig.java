package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.auth.CustomAccessDeniedHandler;
import com.sprint.mission.discodeit.auth.CustomAuthenticationEntryPoint;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.auth.CustomPermissionEvaluator;
import com.sprint.mission.discodeit.auth.LoginFailureHandler;
import com.sprint.mission.discodeit.auth.LoginSuccessHandler;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;
  private final CustomAccessDeniedHandler accessDeniedHandler;
  private final DiscodeitUserDetailsService userDetailsService;
  private final DataSource dataSource;
  private final CustomPermissionEvaluator permissionEvaluator;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, SessionRegistry sessionRegistry)
      throws Exception {
    return http
        .csrf(csrf -> csrf
            .csrfTokenRepository(cookieCsrfTokenRepository()) //Set-Cookie 헤더 설정
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())// CSR 방식을 대비한 쿠키 해석 핸들러 커스텀
            .ignoringRequestMatchers("/h2-console/**") // h2 콘솔에 대한 csrf 설정 비활성화
        )
        .headers(headers -> headers
            .frameOptions(FrameOptionsConfig::sameOrigin) // 같은 오리진 내 프레임 허용
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/csrf-token", "api/auth/login", "api/auth/logout")
            .permitAll() // 로그인 및 csrf 토큰 발급 허용
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll() // 회원 가입 허용
            .requestMatchers("/api/**").authenticated() // 그외 모든 api 요청 인증 필요
            .anyRequest().permitAll() // api 요청이 아닌 다른 요청 허용
        )
        .formLogin(login -> login // form 로그인 활성화
            .loginProcessingUrl("/api/auth/login") // 로그인 url 변경
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
        )
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        )
        .sessionManagement(management -> management
            .sessionConcurrency(concurrency -> concurrency
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false) //기본값 false
                .sessionRegistry(sessionRegistry)
            ))
        .rememberMe(remember -> remember
            .rememberMeServices(rememberMeServices(userDetailsService)))
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_CHANNEL_MANAGER\n" +
        "ROLE_CHANNEL_MANAGER > ROLE_USER");
    return roleHierarchy;
  }

  // 먼저 로드 되야해서 static 필수
  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy, CustomPermissionEvaluator permissionEvaluator) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    handler.setPermissionEvaluator(permissionEvaluator);
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

  @Bean
  public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
    JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
    repo.setDataSource(dataSource);
    return repo;
  }

  @Bean
  public CookieCsrfTokenRepository cookieCsrfTokenRepository() {
    CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
    csrfTokenRepository.setCookieCustomizer(cookie -> cookie
        .sameSite("Lax")
        .path("/")
    );
    return csrfTokenRepository;
  }

  @Bean
  public RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
    //db 기반 저장
    PersistentTokenBasedRememberMeServices rememberMeServices =
        new PersistentTokenBasedRememberMeServices(
            "remember-me-key",
            userDetailsService,
            persistentTokenRepository(dataSource)
        );

    rememberMeServices.setTokenValiditySeconds(7 * 24 * 60 * 60);
    rememberMeServices.setParameter("remember-me");
    // samesite 설정
    rememberMeServices.setCookieCustomizer(cookie -> {
      cookie.setAttribute("SameSite", "Lax");
    });

    return rememberMeServices;
  }
}

