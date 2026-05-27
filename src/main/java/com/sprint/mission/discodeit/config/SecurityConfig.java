package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        // 프론트 쪽에 js 접근 허용
        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfTokenRepository.setCookiePath("/");

        http
                .formLogin(
                        login -> login
                                .loginProcessingUrl("/api/auth/login")
                                .successHandler(loginSuccessHandler)
                                .failureHandler(loginFailureHandler)
                )
                .rememberMe(remember -> remember
                        .key("uniqueAndSecret")        // 토큰 해싱을 위한 고유 키 (절대 노출 금지)
                        .tokenValiditySeconds(1800) // 쿠키 유효 기간 (예: 30일)
                        .userDetailsService(userDetailsService) // 사용자 정보를 다시 조회할 서비스 (필수!)
                        .rememberMeParameter("remember-me") // 프론트 체크박스의 name 속성값
                )
                // 토큰 발급을 위해 모든 요청 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/assets/**", "/favicon.ico").permitAll()
                        // swagger
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // actuator
                        .requestMatchers("/actuator/**").permitAll()
                        // 회원가입
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        // 사용자 권한 수정
                        .requestMatchers("/api/auth/role").hasRole("ADMIN")
                        .requestMatchers("/api/users/**",
                                "/api/channels/**",
                                "/api/binaryContents/**",
                                "/api/messages/**",
                                "/api/readStatuses/**").hasRole("USER")
                        // 로그인, 로그아웃, Csrf 토큰
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                // 토큰 Repo vs Request가 Handler -> Filter가 비교를 실행
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                )
                // HTML 리다이렉트 방지
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage())
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage())
                        )
                )
                // 로그아웃 시 상태코드만 반환 (204 Void)
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)
                        )
                )
                .sessionManagement(management -> management
                        .sessionConcurrency(concurrency -> concurrency
                                .maximumSessions(1)              // 허용 세션 1
                                .sessionRegistry(sessionRegistry())
                                .maxSessionsPreventsLogin(false) // 로그인 시 기존 꺼 만료
                                .expiredSessionStrategy(event ->
                                        event.getResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED, "세션이 만료되었습니다."))
                        )
                );

        return http.build();
    }

    // BCryptPasswordEncoder 빈 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 계층 구조 빈 등록
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("ADMIN").implies("CHANNEL_MANAGER")
                .role("CHANNEL_MANAGER").implies("USER")
                .build();
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

}
