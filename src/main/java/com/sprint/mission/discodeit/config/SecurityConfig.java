package com.sprint.mission.discodeit.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

  // Security 필터 체인 등록
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      LoginSuccessHandler loginSuccessfulHandler,
      LoginFailureHandler loginFailureHandler) throws Exception {
    return http
        .csrf(csrf -> csrf
            .ignoringRequestMatchers(PathRequest.toH2Console())
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )
        .headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions.sameOrigin())
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(PathRequest.toH2Console()).permitAll()
            .anyRequest().permitAll()
        )
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(loginSuccessfulHandler)
            .failureHandler(loginFailureHandler))

        .build();
  }
}
