package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.jwt.TokenRefreshResult;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${discodeit.jwt.refresh-token-validity-seconds}")
    private long refreshTokenValiditySeconds;

    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("refresh")
    public ResponseEntity<JwtDto> refresh(
            @CookieValue(name = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, required = false)
            String refreshToken,
            HttpServletResponse response) {
        TokenRefreshResult result = authService.refresh(refreshToken);

        Cookie refreshTokenCookie = new Cookie(
            JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, result.refreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) refreshTokenValiditySeconds);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new JwtDto(result.accessToken()));
    }

    @PutMapping("role")
    public ResponseEntity<UserDto> updateRole(@RequestBody @Valid UserRoleUpdateRequest request) {
        log.info("사용자 역할 변경 요청: userId={}, newRole={}", request.userId(), request.role());
        UserDto userDto = authService.updateRole(request);
        log.debug("사용자 역할 변경 응답: {}", userDto);
        return ResponseEntity.ok(userDto);
    }
}
