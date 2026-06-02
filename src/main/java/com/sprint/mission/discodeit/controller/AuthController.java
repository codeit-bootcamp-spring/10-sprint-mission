package com.sprint.mission.discodeit.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.auth.JwtRefreshResult;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.RefreshTokenCookieManager;
import com.sprint.mission.discodeit.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

	private final AuthService authService;
	private final RefreshTokenCookieManager refreshTokenCookieManager;

	@GetMapping("/csrf-token")
	@Override
	public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
		String tokenValue = csrfToken.getToken();
		log.debug("CSRF 토큰 요청: {}", tokenValue);

		return ResponseEntity.noContent().build();
	}

	@PostMapping("/refresh")
	@Override
	public ResponseEntity<JwtDto> refresh(
		@CookieValue(
			name = RefreshTokenCookieManager.REFRESH_TOKEN_COOKIE_NAME,
			required = false
		) String refreshToken,
		HttpServletRequest request
	) {
		JwtRefreshResult result = authService.refresh(refreshToken);

		return ResponseEntity.ok()
			.header(
				HttpHeaders.SET_COOKIE,
				refreshTokenCookieManager.create(request, result.refreshToken()).toString()
			)
			.body(result.jwtDto());
	}

	@PutMapping("/role")
	@Override
	public ResponseEntity<UserDto> updateRole(@Valid @RequestBody UserRoleUpdateRequest request) {
		log.debug("사용자 권한 수정 요청: userId={}, newRole={}", request.userId(), request.newRole());

		UserDto updatedUser = authService.updateRole(request);
		return ResponseEntity.ok(updatedUser);
	}
}
