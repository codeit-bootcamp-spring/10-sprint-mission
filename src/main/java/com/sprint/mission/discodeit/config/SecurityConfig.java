package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.handler.*;
import com.sprint.mission.discodeit.service.DiscodeitUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final DiscodeitAccessDeniedHandler discodeitAccessDeniedHandler;
    private final DiscodeitAuthenticationEntryPoint discodeitAuthenticationEntryPoint;
    private final DiscodeitUserDetailsService discodeitUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                // csrf 설정
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
                // 로그인 설정
                .formLogin(login -> login
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler))
                .rememberMe(remember -> remember
                        .rememberMeParameter("remember-me")
                        .rememberMeCookieName("my-remember-me")
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                        .key("my-remember-key")
                        .userDetailsService(discodeitUserDetailsService))
                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/assets/**",
                                "/favicon.ico",
                                "/api/auth/csrf-token",
                                "/api/auth/login",
                                "/api/auth/logout",
                                "/api/auth/csrf-token",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionConcurrency(conCurrency -> conCurrency
                                .maximumSessions(1)
                                .sessionRegistry(sessionRegistry())))
                .exceptionHandling(ex -> ex
                        // 로그인 안한 상태에서 다른 작업 요청 시 예외
                        .authenticationEntryPoint(discodeitAuthenticationEntryPoint)
                        // 권한이 없는 상태에서 해당 작업 요청 시
                        .accessDeniedHandler(discodeitAccessDeniedHandler));
        return http.build();
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository(){
        CookieCsrfTokenRepository repo = new CookieCsrfTokenRepository().withHttpOnlyFalse();
        repo.setParameterName("_csrf");
        repo.setHeaderName("X-XSRF-TOKEN");
        return repo;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 세션 수명 주기 이벤트에 대한 최신 정보를 Spring Security에 제공
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher(){
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry(){
        return new SessionRegistryImpl();
    }
}
