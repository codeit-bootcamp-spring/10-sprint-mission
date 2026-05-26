package com.sprint.mission.discodeit.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

	@Operation(summary = "CSRF 토큰 발급")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "CSRF 토큰 발급 성공")
	})
	ResponseEntity<Void> getCsrfToken(
		@Parameter(hidden = true) CsrfToken csrfToken
	);

	@Operation(summary = "현재 로그인 사용자 조회")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "현재 로그인 사용자 조회 성공")
	})
	ResponseEntity<UserDto> getCurrentUser(
		@Parameter(hidden = true) DiscodeitUserDetails userDetails
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
