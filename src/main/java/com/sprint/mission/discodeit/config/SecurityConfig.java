package com.sprint.mission.discodeit.config;

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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;

import lombok.RequiredArgsConstructor;

@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

	private final LoginSuccessHandler loginSuccessHandler;
	private final LoginFailureHandler loginFailureHandler;
	private final ObjectMapper objectMapper;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, SessionRegistry sessionRegistry) throws Exception {
		http
			.httpBasic(AbstractHttpConfigurer::disable)
			.csrf(csrf -> csrf
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
			)
			.formLogin(login -> login
				.loginProcessingUrl("/api/auth/login")
				.successHandler(loginSuccessHandler)
				.failureHandler(loginFailureHandler)
				.permitAll()
			)
			.logout(logout -> logout
				.logoutUrl("/api/auth/logout")
				.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
			)
			.sessionManagement(management -> management
				.sessionConcurrency(concurrency -> concurrency
					.maximumSessions(1)
					.maxSessionsPreventsLogin(false)
					.sessionRegistry(sessionRegistry)
					.expiredSessionStrategy(event -> {
						ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
						ErrorResponse errorResponse = ErrorResponse.of(
							errorCode,
							"SessionExpiredException",
							errorCode.getMessage()
						);

						event.getResponse().setStatus(errorCode.getHttpStatus().value());
						event.getResponse().setContentType(MediaType.APPLICATION_JSON_VALUE);
						event.getResponse().setCharacterEncoding("UTF-8");
						objectMapper.writeValue(event.getResponse().getWriter(), errorResponse);
					})
				)
			)
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint((request, response, authException) -> {
					ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
					ErrorResponse errorResponse = ErrorResponse.of(
						errorCode,
						authException.getClass().getSimpleName(),
						errorCode.getMessage()
					);

					response.setStatus(errorCode.getHttpStatus().value());
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					response.setCharacterEncoding("UTF-8");
					objectMapper.writeValue(response.getWriter(), errorResponse);
				})
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					ErrorCode errorCode = ErrorCode.FORBIDDEN;
					ErrorResponse errorResponse = ErrorResponse.of(
						errorCode,
						accessDeniedException.getClass().getSimpleName(),
						errorCode.getMessage()
					);

					response.setStatus(errorCode.getHttpStatus().value());
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					response.setCharacterEncoding("UTF-8");
					objectMapper.writeValue(response.getWriter(), errorResponse);
				})
			)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/index.html", "/favicon.ico", "/assets/**").permitAll()
				.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
				.requestMatchers("/actuator/**", "/error").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/users").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
				.anyRequest().authenticated()
			);

		return http.build();
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

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
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
