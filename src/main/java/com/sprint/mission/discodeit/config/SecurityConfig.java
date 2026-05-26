package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.dto.ErrorResponseDTO;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.security.jwt.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.jwt.JwtLogoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.time.Instant;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtLoginSuccessHandler jwtLoginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final JwtLogoutHandler jwtLogoutHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(
                                CookieCsrfTokenRepository.withHttpOnlyFalse()
                        )
                        .csrfTokenRequestHandler(
                                new SpaCsrfTokenRequestHandler()
                        )
                        .ignoringRequestMatchers(
                                "/api/auth/login",
                                "/api/auth/logout",
                                "/api/auth/refresh"
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        // SPA 진입점
                        .requestMatchers("/", "/index.html").permitAll()

                        // 프론트 빌드 파일 경로
                        .requestMatchers("/assets/**").permitAll()

                        .requestMatchers("/api/auth/csrf-token").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()      // 회원가입 경로만 허용
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()      // refreshToken

                        // Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Actuator
                        .requestMatchers("/actuator/**").permitAll()

                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

                            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                                    Instant.now(),
                                    errorCode.getStatus(),
                                    errorCode.getCode(),
                                    errorCode.getMessage(),
                                    Map.of(),
                                    authException.getClass().getSimpleName()
                            );

                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json;charset=UTF-8");

                            response.getWriter()
                                    .write(objectMapper.writeValueAsString(errorResponse));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            ErrorCode errorCode = ErrorCode.ACCESS_DENIED;

                            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                                    Instant.now(),
                                    errorCode.getStatus(),
                                    errorCode.getCode(),
                                    errorCode.getMessage(),
                                    Map.of(),
                                    accessDeniedException.getClass().getSimpleName()
                            );

                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json;charset=UTF-8");

                            response.getWriter()
                                    .write(objectMapper.writeValueAsString(errorResponse));
                        })
                )
                .formLogin(login -> login
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(jwtLoginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler(jwtLogoutHandler)
                        .logoutSuccessHandler(
                                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)
                        )
                )
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
        ;

        return http.build();
    }
}
