package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.dto.ErrorResponseDTO;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
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
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.time.Instant;
import java.util.Map;

@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;

    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            SessionRegistry sessionRegistry
    ) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(
                                CookieCsrfTokenRepository.withHttpOnlyFalse()
                        )
                        .csrfTokenRequestHandler(
                                new SpaCsrfTokenRequestHandler()
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
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                )
                .rememberMe(rememberMe -> rememberMe
                        .rememberMeParameter("remember-me")
                        // remember-me 쿠키 이름
                        .rememberMeCookieName("remember-me")
                        .tokenValiditySeconds(60 * 60 * 24 *14)     // 14일
                        // remember-me 토큰 서명에 사용하는 키
                        .key("discodeit-remember-me-key")
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(
                                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)
                        )
                )
                .sessionManagement(management -> management
                        .sessionConcurrency(concurrency -> concurrency
                                // 계정 하나당 세션 하나만 허용
                                .maximumSessions(1)
                                // 새 로그인이 들어오면 기존 세션을 만료시키고 새로운 로그인을 허용
                                .maxSessionsPreventsLogin(false)
                                .sessionRegistry(sessionRegistry)
                        )
                )
        ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
                ROLE_ADMIN > ROLE_CHANNEL_MANAGER
                ROLE_CHANNEL_MANAGER > ROLE_USER
                """);
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            RoleHierarchy roleHierarchy
    ) {
        DefaultMethodSecurityExpressionHandler handler =
                new DefaultMethodSecurityExpressionHandler();

        handler.setRoleHierarchy(roleHierarchy);
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
}
