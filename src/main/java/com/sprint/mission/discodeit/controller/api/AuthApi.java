package com.sprint.mission.discodeit.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

	@Operation(summary = "CSRF 토큰 발급")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "203", description = "CSRF 토큰 발급 성공")
	})
	ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken);
}
