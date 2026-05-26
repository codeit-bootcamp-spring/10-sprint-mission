package com.sprint.mission.discodeit.security;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.security.jwt.JwtProperties;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RefreshTokenCookieManager {

	public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

	private final JwtProperties jwtProperties;

	public ResponseCookie create(HttpServletRequest request, String refreshToken) {
		return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
			.httpOnly(true)
			.secure(request.isSecure())
			.sameSite("Lax")
			.path("/")
			.maxAge(jwtProperties.getRefreshTokenValiditySeconds())
			.build();
	}

	public ResponseCookie expire(HttpServletRequest request) {
		return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
			.httpOnly(true)
			.secure(request.isSecure())
			.sameSite("Lax")
			.path("/")
			.maxAge(0)
			.build();
	}
}
