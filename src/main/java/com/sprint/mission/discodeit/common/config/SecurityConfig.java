package com.sprint.mission.discodeit.common.config;

import com.sprint.mission.discodeit.auth.LoginFailureHandler;
import com.sprint.mission.discodeit.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.jwt.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.jwt.JwtLogoutHandler;
import com.sprint.mission.discodeit.jwt.JwtRegistry;
import com.sprint.mission.discodeit.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      JwtLoginSuccessHandler jwtloginSuccessHandler,
      LoginFailureHandler loginFailureHandler,
      /*DiscodeitUserDetailsService discodeitUserDetailsService,*/
      JwtLogoutHandler jwtLogoutHandler,
      JwtAuthenticationFilter jwtAuthenticationFilter)
      throws Exception {
    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(jwtloginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .addLogoutHandler(jwtLogoutHandler)
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/api/auth/csrf-token",
                "/api/users",
                "/api/auth/login",
                "/api/auth/logout",
                "/api/auth/refresh",
                "/v3/api-docs/**",
                "/actuator/**",
                "/",
                "/index.html",
                "/assets/**",
                "/favicon.ico",
                "/script.js",
                "/styles.css",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/ws/**",
                "/pub/**",
                "/sub/**"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex
            // 인증 실패 (로그인 안함) 401 처리
            .authenticationEntryPoint((request, response, authException) -> {
              response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            })
            // 인가 실패 (권한 없음) 403처리
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              response.sendError(HttpServletResponse.SC_FORBIDDEN);
            })
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
    ;
//        .rememberMe(remember -> remember
//            .key("my-remember-key")
//            .tokenValiditySeconds(7 * 24 * 60 * 60)
//            .rememberMeParameter("remember-me")
//            .userDetailsService(discodeitUserDetailsService)
//        )

    return http.build();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    roleHierarchy.setHierarchy(
        "ROLE_ADMIN > ROLE_CHANNEL_MANAGER\nROLE_CHANNEL_MANAGER > ROLE_USER");
    return roleHierarchy;
  }

  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(
      JwtTokenProvider jwtTokenProvider,
      UserDetailsService userDetailsService,
      JwtRegistry jwtRegistry) {
    return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService, jwtRegistry);
  }

//  @Bean
//  public SessionRegistry sessionRegistry() {
//    return new SessionRegistryImpl();
//  }
//
//  @Bean
//  public HttpSessionEventPublisher httpSessionEventPublisher() {
//    return new HttpSessionEventPublisher();
//  }
}
