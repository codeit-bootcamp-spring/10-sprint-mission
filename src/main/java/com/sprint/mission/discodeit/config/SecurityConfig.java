package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.security.jwt.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.jwt.JwtLogoutHandler;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      JwtAuthenticationFilter jwtAuthenticationFilter,
      JwtLoginSuccessHandler jwtLoginSuccessHandler,
      LoginFailureHandler loginFailureHandler,
      JwtLogoutHandler jwtLogoutHandler) throws Exception {
    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
            // WebSocket 핸드셰이크/SockJS HTTP 폴백은 CSRF 검사에서 제외 (인증은 STOMP CONNECT 헤더로 처리)
            // 제외 안하면 .withSockJS()에서 문제 발생 (SockJS HTTP 폴백 요청이 토큰 없어 403으로 차단됨)
            .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/ws/**"))
        )
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(jwtLoginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        .authorizeHttpRequests(auth -> auth
            // SSE 등 비동기 요청은 최초 REQUEST 디스패치에서 이미 인증/인가됨.
            // SseEmitter 반환 후 ASYNC 재디스패치 시 JwtAuthenticationFilter(OncePerRequestFilter)가
            // 다시 실행되지 않아 SecurityContext가 비어 Access Denied가 발생하므로 ASYNC/ERROR는 통과시킴으로서 문제 해결
            .dispatcherTypeMatchers(DispatcherType.ASYNC, DispatcherType.ERROR).permitAll()
            .requestMatchers(
                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/auth/csrf-token"),
                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/users"),
                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/login"),
                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/logout"),
                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/refresh"),
                new NegatedRequestMatcher(AntPathRequestMatcher.antMatcher("/api/**"))
            ).permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) -> {
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.setContentType(MediaType.APPLICATION_JSON_VALUE);
              response.setCharacterEncoding("UTF-8");
              response.getWriter().write("{\"message\":\"인증이 필요합니다.\"}");
            })
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              response.setStatus(HttpServletResponse.SC_FORBIDDEN);
              response.setContentType(MediaType.APPLICATION_JSON_VALUE);
              response.setCharacterEncoding("UTF-8");
              response.getWriter().write("{\"message\":\"접근 권한이 없습니다.\"}");
            })
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .addLogoutHandler(jwtLogoutHandler)
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)
            )
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        // (UsernamePasswordAuthenticationFilter 앞에) JwtAuthenticationFilter 추가
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.withDefaultRolePrefix()
        .role("ADMIN").implies("CHANNEL_MANAGER")
        .role("CHANNEL_MANAGER").implies("USER")
        .build();
  }

  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }

  // @Component 사용하면 이중 등록 되는 문제가 발생함
  // 이중 등록 시 SecurityContextHolderFilter가 컨텍스트를 다시 빈 컨텍스트로 덮어 씌우는 문제 발생
  // JwtAuthenticationFilter는 OncePerRequestFilter 이기 때문에 뒤(Spring Security 레이어)에서 다시 실행도 안됨
  // -> @Bean으로 생성하여 해결 (단일 생성 등록)
  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(
      JwtTokenProvider jwtTokenProvider,
      UserDetailsService userDetailsService,
      JwtRegistry jwtRegistry) {
    return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService, jwtRegistry);
  }
}
