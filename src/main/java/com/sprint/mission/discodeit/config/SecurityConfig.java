package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.auth.handler.*;
import com.sprint.mission.discodeit.service.details.DiscodeitUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, LoginSuccessHandler loginSuccessHandler, LoginFailureHandler loginFailureHandler, AuthenticationEntryPoint authenticationEntryPoint, AccessDeniedHandler accessDeniedHandler, SessionRegistry sessionRegistry, UserDetailsService userDetailsService, DiscodeitUserDetailsService discodeitUserDetailsService, JwtLoginSuccessHandler jwtLoginSuccessHandler, JwtLogoutHandler jwtLogoutHandler) throws Exception {
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/favicon.ico",
                                "/index.html",
                                "/assets/**",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()
                        .requestMatchers(
                                "/api/auth/csrf-token",
                                "/api/auth/login",
                                "/api/auth/logout",
                                "/api/auth/refresh",
                                "/api/users",
                                "/swagger-ui/**",
                                "/actuator/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(jwtLoginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                )
//                .loginPage("/auths/login-form")
//                .loginProcessingUrl("/process_login")
//                .failureUrl("/auths/login-form?error")
                /*
                .rememberMe(remember -> remember
                        .rememberMeParameter("remember-me")
                        .rememberMeCookieName("my-rememeber-me")
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                        .key("my-remember-key")
                        .userDetailsService(discodeitUserDetailsService)
                )
                 */
                // 로그아웃 관련 설정 추가
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(
                                HttpStatus.NO_CONTENT
                        ))
                        .addLogoutHandler(jwtLogoutHandler)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(session -> session
                        .sessionFixation(fixation -> fixation.changeSessionId())
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
                ROLE_ADMIN > ROLE_CHANNEL_MANAGER
                ROLE_CHANNEL_MANAGER > ROLE_USER
                """);
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
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
