package com.sprint.mission.discodeit.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;

import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

	@Operation(summary = "CSRF 토큰 발급")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "CSRF 토큰 발급 성공")
	})
	ResponseEntity<Void> getCsrfToken(
		@Parameter(hidden = true) CsrfToken csrfToken
	);

	@Operation(summary = "액세스 토큰 재발급")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", description = "액세스 토큰 재발급 성공",
			content = @Content(schema = @Schema(implementation = JwtDto.class))
		),
		@ApiResponse(responseCode = "401", description = "리프레시 토큰이 유효하지 않음")
	})
	ResponseEntity<JwtDto> refresh(
		@Parameter(hidden = true) String refreshToken,
		@Parameter(hidden = true) HttpServletRequest request
	);

	@Operation(summary = "사용자 권한 수정")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", description = "사용자 권한 수정 성공",
			content = @Content(schema = @Schema(implementation = UserDto.class))
		),
		@ApiResponse(responseCode = "404", description = "User를 찾을 수 없음")
	})
	ResponseEntity<UserDto> updateRole(UserRoleUpdateRequest request);
}
