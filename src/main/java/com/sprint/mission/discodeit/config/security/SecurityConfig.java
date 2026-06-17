package com.sprint.mission.discodeit.config.security;

import com.sprint.mission.discodeit.security.filter.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.security.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.security.handler.RestAccessDeniedHandler;
import com.sprint.mission.discodeit.security.handler.RestAuthenticationEntryPoint;
import com.sprint.mission.discodeit.security.handler.jwt.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.handler.jwt.JwtLogoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true) // Method Security 활성화
public class SecurityConfig {

    private final JwtLoginSuccessHandler jwtLoginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtLogoutHandler jwtLogoutHandler;

    // SecurityFilterChain Bean 등록
    // HttpSecurity를 통해 HTTP 요청에 대한 보안 설정 구성
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                )
                .formLogin(login -> login
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(jwtLoginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler(jwtLogoutHandler)
                        .logoutSuccessHandler(
                                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                )
                .authorizeHttpRequests(auth -> auth
                        // Auth
                        .requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                        // 회원가입
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        // Swagger / API Docs
                        .requestMatchers(
                                "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs", "/v3/api-docs/**"
                        ).permitAll()
                        // Actuator (prod일 때 조정 필요)
                        .requestMatchers("/actuator/**").permitAll()
                        // 프론트엔드
                        .requestMatchers("/", "/index.html", "/assets/**", "/favicon.ico").permitAll()
                        // 그 외 나머지 `/api/**` 요청
                        .requestMatchers("/api/**").authenticated()
                        // 그 외 나머지 `/api/**` 가 아닌 요청
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        // 인증되지 않은 사용자가 인증이 필요한 API에 접근했을 때 실행
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        // 인증이 되었지만 권한이 부족한 사용자가 접근했을 때 실행
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                .sessionManagement(management -> management
                        // JWT 기반 토큰 기반 인증을 사용으로 인증 상태를 서버 세션에 저장하지 않도록 설정
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        SecurityFilterChain chain = http.build();

//        log.debug("========== [Spring Security Filter List - START] ==========");
//        chain.getFilters().forEach(filter ->
//                log.debug("{}", filter.getClass().getSimpleName())
//        );
//        log.debug("========== [Spring Security Filter List - END] ==========");

        return chain;
    }

    // Role Hierarchy (권한 계층 구조)
    @Bean
    public RoleHierarchy roleHierarchy() {
        // builder 방식
        // `withDefaultRolePrefix` 사용 시 `ROLE_` prefix를 자동으로 붙여줌
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("ADMIN").implies("CHANNEL_MANAGER")
                .role("CHANNEL_MANAGER").implies("USER")
                .build();
    }

    // Method Security 표현식을 처리하는 핸들러 설정
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        // @PreAuthorize, @PostAuthorize 같은 Method Security 표현식을 처리하는 핸들러
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();

        // Method Security에서도 RoleHierarchy가 적용되도록 설정
        handler.setRoleHierarchy(roleHierarchy);

        return handler;
    }

    // PasswordEncoder Bean 등록
    // 비밀번호를 bcrypt 알고리즘으로 해시 처리 - 같은 비밀번호라도 다른 해시값 생성
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
