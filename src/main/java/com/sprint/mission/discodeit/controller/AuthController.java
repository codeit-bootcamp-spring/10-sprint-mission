package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.auth.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.auth.JwtDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.auth.TokenDto;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

/*
    TODO: Swagger API 명세서 세부 작업
 */
@Slf4j
@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "CSRF 토큰 발급", operationId = "CSRFToken")
    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();

        log.debug("CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
    }

    @Operation(summary = "AccessToken 재발급", operationId = "refreshToken")
    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refreshToken(@CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken,
                                               HttpServletResponse response) {
        TokenDto tokenDto = authService.reissueRefreshToken(refreshToken);

        // 리프레시 토큰을 저장할 쿠키 객체
        Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshTokenCookie.setHttpOnly(true);               // 자바스크립트 읽기 방지
        refreshTokenCookie.setPath("/");                    // 모든 경로에서 사용
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 14);    // 유효 기간 (14일)
        response.addCookie(refreshTokenCookie);             // 응답 헤더 내 포함

        return ResponseEntity.ok(tokenDto.jwtDto());
    }

    @Operation(summary = "사용자 권한 수정", operationId = "updateRole")
    @PutMapping("/role")
    public ResponseEntity<UserDto> updateUserRole(@Valid @RequestBody RoleUpdateRequest roleUpdateRequest) {
        UserDto response = authService.updateUserRole(roleUpdateRequest);

        return ResponseEntity.ok(response);
    }
}
