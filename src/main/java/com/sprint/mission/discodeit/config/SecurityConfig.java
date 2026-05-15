package com.sprint.mission.discodeit.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final SpaCsrfTokenRequestHandler spaCsrfTokenRequestHandler;
  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;

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
        );

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
