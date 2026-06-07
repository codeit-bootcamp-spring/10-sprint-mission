package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

  @Operation(summary = "CSRF 토큰 발급")
  @ApiResponse(responseCode = "203", description = "CSRF 토큰 발급 성공 (Set-Cookie 헤더에 포함)")
  ResponseEntity<Void> getCsrfToken(
      @Parameter(hidden = true) CsrfToken csrfToken
  );

  @Operation(summary = "사용자 권한 변경 (ADMIN 전용)")
  @ApiResponse(responseCode = "200", description = "권한 변경 성공")
  ResponseEntity<UserDto> updateRole(@RequestBody UserRoleUpdateRequest request);

  @Operation(summary = "Access Token 재발급")
  @ApiResponse(responseCode = "200", description = "새 Access Token 반환 및 Refresh Token 쿠키 갱신")
  @ApiResponse(responseCode = "401", description = "Refresh Token 없음 또는 유효하지 않음")
  ResponseEntity<JwtDto> refresh(
      @Parameter(hidden = true) @CookieValue(value = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
      @Parameter(hidden = true) HttpServletResponse response
  );
}
