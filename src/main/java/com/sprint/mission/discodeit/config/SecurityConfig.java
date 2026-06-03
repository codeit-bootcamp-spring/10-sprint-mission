package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.jwt.filter.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.security.jwt.handler.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.jwt.handler.JwtLogoutHandler;
import com.sprint.mission.discodeit.security.session.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.security.session.handler.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.service.basic.DiscodeitUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/*
    SecurityConfig
    --------------
    프로젝트 전체의 보안 통제 및 보안 필터 조립
 */
@Configuration
@EnableWebSecurity(debug = true)        // 필터 목록 콘솔에 출력
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtLoginSuccessHandler jwtLoginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final JwtLogoutHandler jwtLogoutHandler;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 메인 보안 필터 라인 조립 및 요청별 출입 통제 규칙 정의
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            SessionRegistry sessionRegistry,
            DiscodeitUserDetailsService discodeitUserDetailsService) throws Exception {
        // HTTP 보안 설정
        http
                // 폼 로그인 활성화 및 로그인 처리 주소 지정
                .formLogin(login -> login
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(jwtLoginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                )
                // CSRF (크로스 사이트 요청 위조) 방어 설정
                .csrf(csrf -> csrf
                        // 토큰을 쿠키에 저장하되, 프론트가 읽을 수 있도록 HttpOnly 방어막 해제
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // 토큰 검증 주제 설정
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                )
                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        // 로그아웃 시, JWT 핸들러 실행
                        .addLogoutHandler(jwtLogoutHandler)
                        // 로그아웃 시, 페이지 리다이렉션 대신 204 상태 코드 반환
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                        // 세선 및 쿠키 삭제
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                // 세션 (Session) 설정
                .sessionManagement(session -> session
                        // 세션을 생성하지 않고, SecurityContext를 유지하지 않음
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 인가 (Authorization) 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증에서 제외되는 요청
                        .requestMatchers(
                                HttpMethod.POST, "/api/users"           // 회원가입
                        ).permitAll()
                        .requestMatchers(
                                "/", "/index.html",                   // 명시적 접근
                                "/static/**", "/assets/**",           // 기본 정적 폴더
                                "/favicon.ico","/css/**", "/js/**", // 기타 자원
                                "/api/auth/csrf-token",                 // CSRF Token 발급
                                "/api/auth/login",                      // 로그인
                                "/api/auth/logout",                     // 로그아웃
                                "/api/auth/refresh",                    // refresh token 재발급
                                "/docs",                                // Swagger UI 접속 주소
                                "/api-docs",                            // API 명세서 경로
                                "/api-docs/**",                         // Swagger 문서 데이터
                                "/swagger-ui/**",                       // Swagger UI 정적 파일 (CSS, JS) 경로
                                "/swagger-ui.html",                     // Swagger UI 기본 페이지 (리다이렉트 대비용)
                                "/actuator/**",                         // Actuator 서버 상태 체크
                                "/error"                                // 기본 에러 페이지 처리
                        ).permitAll()
                        // 모든 요청에 대해 권한 검증
                        .anyRequest().authenticated()
                )
                // 예외 처리 핸들러 설정
                .exceptionHandling(ex -> ex
                        // 비로그인 사용자의 요청 발생 시, 401 Unauthorized 반환
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"errorCode\": \"UNAUTHORIZED\", \"message\": \"로그인이 필요합니다.\"}");
                        })
                        // 요청자의 권한이 부적합한 경우, 403 Forbidden 반환
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"errorCode\": \"FORBIDDEN\", \"message\": \"접근 권한이 없습니다.\"}");
                        })
                )
                // JWT 필터 우선 실행
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
