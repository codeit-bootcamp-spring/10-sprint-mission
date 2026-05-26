package com.sprint.mission.discodeit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

	private final AuthService authService;
	private final UserService userService;

	@GetMapping("/csrf-token")
	@Override
	public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
		String tokenValue = csrfToken.getToken();
		log.debug("CSRF 토큰 요청: {}", tokenValue);

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/me")
	@Override
	public ResponseEntity<UserDto> getCurrentUser(
		@AuthenticationPrincipal DiscodeitUserDetails userDetails
	) {
		UserDto principalUser = userDetails.getUserDto();
		UserDto currentUser = userService.find(principalUser.id());
		log.debug("현재 로그인 사용자 조회 요청: userId={}", currentUser.id());

		return ResponseEntity.ok(currentUser);
	}

	@PutMapping("/role")
	@Override
	public ResponseEntity<UserDto> updateRole(@Valid @RequestBody UserRoleUpdateRequest request) {
		log.debug("사용자 권한 수정 요청: userId={}, newRole={}", request.userId(), request.newRole());

		UserDto updatedUser = authService.updateRole(request);
		return ResponseEntity.ok(updatedUser);
	}
}
