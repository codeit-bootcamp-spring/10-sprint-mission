package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.DiscodeitAccessDeniedHandler;
import com.sprint.mission.discodeit.security.DiscodeitAuthenticationEntryPoint;
import com.sprint.mission.discodeit.security.HttpStatusReturningLogoutSuccessHandler;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  private final SpaCsrfTokenRequestHandler spaCsrfTokenRequestHandler;
  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;
  private final HttpStatusReturningLogoutSuccessHandler logoutSuccessHandler;
  private final DiscodeitAuthenticationEntryPoint authenticationEntryPoint;
  private final DiscodeitAccessDeniedHandler accessDeniedHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(spaCsrfTokenRequestHandler)
        )
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(logoutSuccessHandler)
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/").permitAll()
            .requestMatchers("/index.html").permitAll()
            .requestMatchers("/api/auth/csrf-token").permitAll()
            .requestMatchers("/api/auth/login").permitAll()
            .requestMatchers("/api/auth/logout").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(e -> e
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        )
        .sessionManagement(management -> management
            .sessionConcurrency(concurrency -> concurrency
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)
                .sessionRegistry(sessionRegistry())
            )
        );

    return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring()
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
        .requestMatchers("/assets/**");
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
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
