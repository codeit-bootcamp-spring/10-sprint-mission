package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

	private final LoginSuccessHandler loginSuccessHandler;
	private final LoginFailureHandler loginFailureHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
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
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/index.html", "/favicon.ico", "/assets/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
				.anyRequest().permitAll()
			);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
