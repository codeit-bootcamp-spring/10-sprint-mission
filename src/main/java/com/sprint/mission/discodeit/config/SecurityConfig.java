package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.auth.LoginFailureHandler;
import com.sprint.mission.discodeit.auth.LoginSuccessHandler;
import com.sprint.mission.discodeit.auth.SpaCsrfTokenRequestHandler;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

//@Configuration: Spring 설정 클래스
@Configuration
@RequiredArgsConstructor

//Method Security 활성화
@EnableMethodSecurity
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;

    // 애플리케이션의 HTTP 요청에 적용될 Spring Security 필터 묶음.
    // Spring Security는 내부적으로 여러 보안 설정을 HttpSecurity에 쌓아두고,
    // 마지막에 build()를 호출하면 실제 요청을 가로채는 SecurityFilterChain 객체를 만든다.
    // 즉, 요청 처리 전에 실행될 SecurityFilterChain을 등록하는 코드.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CsrfTokenRepository는 Spring Security가 CSRF 토큰을 어디에 저장하고, 어디서 다시 꺼내 검증할지를 담당하는 인터페이스
                // 기본값은 보통 HttpSessionCsrfTokenRepository이다. -> CSRF 토큰을 서버 세션(HttpSession)에 저장
                // CookieCsrfTokenRepository: CSRF 토큰을 쿠키에 저장한다.
                // CSRF 토큰을 쿠키에 저장 -> 클라이언트가 쿠키에서 토큰을 읽음 -> 이후 요청때 토큰을 헤더로 다시 보냄
                // HttpOnly=true 인 쿠키는 브라우저에는 저장되지만 JavaScript에서 접근할 수 없다.
                // discodeit은 CSR방식이라서 프론트(JS)가 쿠키에 담긴 CSRF 토큰을 읽어야하기때문에 HttpOnly=false로 설정한다.
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        //SPA일때와 SSR방식일때 CSRF 토큰을 어떻게 읽을지와 어떻게 노출할지를 결정하는 Handler
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                )
                // 폼 로그인 기능을 기본 설정으로 활성화 한다.
                /**
                 UsernamePasswordAuthenticationFilter: POST /login 처리
                 DefaultResourcesFilter: Spring Security 기본 로그인/로그아웃 페이지에 필요한 기본 리소스 처리
                 DefaultLoginPageGeneratingFilter: 커스텀 로그인 페이지를 지정하지 않았을 때 기본 로그인 페이지 생성
                 DefaultLogoutPageGeneratingFilter: 커스텀 로그아웃 페이지를 지정하지 않았을 때 기본 로그아웃 페이지 생성
                 네개 필터가 추가된다.
                 **/
                .formLogin(login -> login
                        .loginProcessingUrl("/api/auth/login")//로그인을 처리할 url
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                )
                /**
                 [로그아웃 요청 흐름]
                 (1) 클라이언트가 POST /api/auth/logout 요청
                 (2) 브라우저가 JSESSIONID 쿠키 자동 포함
                 (3) Spring Security LogoutFilter가 요청을 처리
                 (4) SecurityContext 비움.
                 (5) 세션 무효화
                 (6) JSESSIONID 쿠키 정리
                 (7) LogoutSuccessHandelr 실행 -> 204 No Content 응답

                 HttpStatusReturningLogoutSuccessHandler: 로그아웃 성공 후 지정한 HTTP STATUS만 반환
                 204 No Content 반환.
                 **/
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                )
                /**
                 GET /api/auth/me 이 API는 현재 로그인한 사용자의 정보를 반환하기때문에 인증이 필요하다.

                 --로그인 성공후 응답--
                 HTTP/1.1 200 OK
                 Set-Cookie: JSESSIONID=A1B2C3D4E5F6; Path=/; HttpOnly
                 Content-Type: application/json

                 (1)브라우저는 JSESSIONID 쿠키를 저장한다.
                 JSESSIONID=A1B2C3D4E5F6

                 (2)프론트가 현재 로그인한 사용자 정보를 확인하려고 /api/auth/me를 호출한다.
                 GET /api/auth/me
                 Cookie: JSESSIONID=A1B2C3D4E5F6

                 (3) 이 요청이 들어오면 아래 설정을 거친다.
                 authenticated(): 인증된 사용자만 통과 가능
                 **/
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/me").authenticated()
//                        .anyRequest().permitAll() //따로지정하지않은 모든 요청은 인증없이 허용
//                )

                //// csrf 토큰 발행, 로그인, 로그아웃, 회원가입은 인증이 필요없다.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/csrf-token",
                                "/api/auth/login",
                                "/api/auth/logout"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        ///권한 수정은 ADMIN만 가능하다.
                        .requestMatchers("/api/auth/role").hasRole("ADMIN")
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/assets/**",
                                "/favicon.ico",
                                "/error",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/**"
                        ).permitAll()
                        .anyRequest().authenticated()//외에 모든 요청은 인증 필요.
                )
                /**
                 exceptionHandling: Spring Security 필터 안에서 발생한 인증/인가 예외를 어떻게 HTTP 응답으로 바꿀지 정하는 설정
                 @ExceptionHandler와 처리 위치가 다르다. -> 인증/권한문제는 Controller 도착전에 Filter에서 막히는 경우가 많다.
                 @RestControllerAdvice의 @ExceptionHandler만으로 처리되지 않을 수 있다.
                 -> 그래서 exceptionHandling을 따로 설정.
                 **/
                .exceptionHandling(ex -> ex
                        //인증 자체가 안된경우(401).
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        })
                        //인증은 됐지만 권한이 부족한경우(403).
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        })
                );
        return http.build();
    }

    //PasswordEncoder: 비밀번호 인코딩/검증 추상화
    //BCryptPasswordEncoder: BCrypt 알고리즘을 사용하는 구현체
    //UserService에서 비밀번호 해시값으로 변환할때 사용.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     RoleHierarchy: "상위 권한이 하위권한을 자동으로 포함한다"는 규칙을 Spring Security에 알려주는 기능.
     ADMIN > CHANNEL_MANAGER > USER
     ADMIN = ADMIN + CHANNEL_MANAGER + USER
     이걸 써야 ADMIN일때 hasRole("CHANNEL_MANAGER")를 통과한다.
     **/
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
        ROLE_ADMIN > ROLE_CHANNEL_MANAGER
        ROLE_CHANNEL_MANAGER > ROLE_USER
        """);
    }

    /**
     method security에서 RoleHierarchy를 적용하기위한 Bean
     MethodSecurityExpressionHandler: @PreAuthorize("hasRole('ADMIN')")같은 표현식을 해석하는 담당자.
     **/
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler =
                new DefaultMethodSecurityExpressionHandler();

        handler.setRoleHierarchy(roleHierarchy);

        //설정완료된 handler를 Spring Security에 등록.
        return handler;
    }
}
