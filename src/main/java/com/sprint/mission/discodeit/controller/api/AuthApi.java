package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.JwtDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserRoleUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Tag(name = "Auth", description = "인증 및 보안 관련 API")
public interface AuthApi {

    @Operation(summary = "CSRF 토큰 발급", description = "SPA 클라이언트를 위한 CSRF 토큰을 발급하고 세션을 초기화합니다.")
    @GetMapping("/csrf-token")
    ResponseEntity<Map<String, String>> getCsrfToken(CsrfToken csrfToken);

    @Operation(summary = "사용자 권한 변경", description = "특정 사용자의 권한을 변경합니다. (관리자 권한 필요)")
    @PutMapping("/role")
    ResponseEntity<UserDto.Response> updateRole(@RequestBody @Valid UserRoleUpdateRequest request);

    @Operation(summary = "액세스 토큰 갱신", description = "리프레시 토큰 쿠키를 사용하여 새로운 액세스 토큰을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공",
                    content = @Content(schema = @Schema(implementation = JwtDto.class))),
            @ApiResponse(responseCode = "401", description = "유효하지 않거나 만료된 리프레시 토큰")
    })
    @PostMapping("/refresh")
    ResponseEntity<JwtDto> refresh(
            @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    );
}

