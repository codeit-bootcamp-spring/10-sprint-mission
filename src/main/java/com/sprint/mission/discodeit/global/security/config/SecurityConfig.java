package com.sprint.mission.discodeit.global.security.config;

import com.sprint.mission.discodeit.auth.domain.provider.JwtRegistry;
import com.sprint.mission.discodeit.auth.infra.security.handler.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.global.security.constant.SecurityConstants;
import com.sprint.mission.discodeit.global.security.filter.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.global.security.handler.ForbiddenAccessHandler;
import com.sprint.mission.discodeit.global.security.handler.UnauthenticatedEntryPoint;
import com.sprint.mission.discodeit.global.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.global.web.ws.constant.WebSocketConstants;
import java.util.stream.Stream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      AuthenticationSuccessHandler loginSuccessHandler,
      AuthenticationFailureHandler loginFailureHandler,
      LogoutHandler jwtLogoutHandler,
      JwtAuthenticationFilter jwtAuthenticationFilter)
      throws Exception {
    return http.csrf(csrf -> csrf
            .ignoringRequestMatchers(WebSocketConstants.WS_MATCHER)
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(
            session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .formLogin(form ->
            form.loginProcessingUrl(SecurityConstants.LOGIN_URL)
                .usernameParameter(SecurityConstants.USERNAME_PARAMETER)
                .passwordParameter(SecurityConstants.PASSWORD_PARAMETER)
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler))
        .logout(logout -> logout.logoutUrl(SecurityConstants.LOGOUT_URL)
            .addLogoutHandler(jwtLogoutHandler)
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
            .permitAll())
        .httpBasic(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(WebSocketConstants.WS_MATCHER).permitAll()
                    .requestMatchers(HttpMethod.GET, SecurityConstants.PUBLIC_GET_PATHS).permitAll()
                    .requestMatchers(HttpMethod.POST, SecurityConstants.PUBLIC_POST_PATHS) .permitAll()
                    .anyRequest().authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            exception -> exception
                .authenticationEntryPoint(new UnauthenticatedEntryPoint())
                .accessDeniedHandler(new ForbiddenAccessHandler()))
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(SecurityConstants.CORS_ALLOWED_ORIGINS);
    config.setAllowedMethods(
        Stream.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.PATCH, HttpMethod.DELETE,
                HttpMethod.OPTIONS)
            .map(HttpMethod::toString).toList());
    config.setAllowedHeaders(SecurityConstants.CORS_ALLOWED_HEADERS);
    config.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration(SecurityConstants.CORS_REGISTER_PATTERN, config);
    return source;
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
      JwtRegistry jwtRegistry) {
    return new JwtAuthenticationFilter(jwtTokenProvider, jwtRegistry);
  }
}
