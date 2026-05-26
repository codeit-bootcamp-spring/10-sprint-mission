package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "Auth API")
public interface AuthApi {

  @Operation(summary = "CSRF 토큰 발급")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "203",
          description = "CSRF 토큰 발급 성공"
      )
  })
  ResponseEntity<Void> getCsrfToken(
      @Parameter(hidden = true) CsrfToken csrfToken
  );

  @Operation(summary = "사용자 권한 변경 (ADMIN 전용)")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "권한 변경 성공",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "잘못된 입력값 (Validation 실패)",
          content = @Content(examples = @ExampleObject(value = "대상 사용자 ID를 입력하세요. | 변경할 권한을 입력하세요."))
      ),
      @ApiResponse(
          responseCode = "403",
          description = "권한 없음 (관리자가 아님)",
          content = @Content(examples = @ExampleObject(value = "Access Denied"))
      )
  })
  ResponseEntity<UserDto> updateRole(
      @Parameter(description = "변경할 대상의 ID와 새로운 권한 정보") @RequestBody UserRoleUpdateRequest request
  );

  @Operation(summary = "엑세스 토큰 재발급 (Refresh Token 활용)")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "토큰 재발급 성공",
          content = @Content(schema = @Schema(implementation = JwtDto.class))
      ),
      @ApiResponse(
          responseCode = "401",
          description = "유효하지 않거나 만료된 리프레시 토큰",
          content = @Content(examples = @ExampleObject(value = "Invalid refresh token"))
      )
  })
  ResponseEntity<JwtDto> refresh(
      @Parameter(hidden = true) String refreshToken,
      @Parameter(hidden = true) HttpServletResponse response
  );
}
