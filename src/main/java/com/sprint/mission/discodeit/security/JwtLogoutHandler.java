package com.sprint.mission.discodeit.security;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.security.jwt.JwtRegistry;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtLogoutHandler implements LogoutHandler {

	private final RefreshTokenCookieManager refreshTokenCookieManager;
	private final JwtRegistry jwtRegistry;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		resolveRefreshToken(request)
			.ifPresent(jwtRegistry::invalidateJwtInformationByRefreshToken);
		response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookieManager.expire(request).toString());
	}

	private Optional<String> resolveRefreshToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return Optional.empty();
		}

		return Arrays.stream(cookies)
			.filter(cookie -> RefreshTokenCookieManager.REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
			.map(Cookie::getValue)
			.findFirst();
	}
}
