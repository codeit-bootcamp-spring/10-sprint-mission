package com.sprint.mission.discodeit.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtLogoutHandler implements LogoutHandler {

	private final RefreshTokenCookieManager refreshTokenCookieManager;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookieManager.expire(request).toString());
	}
}
