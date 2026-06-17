package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.JwtDto;
import com.sprint.mission.discodeit.dto.JwtRefreshResultDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserRoleUpdateRequestDto;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Auth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${discodeit.security.cookie.secure:false}")
    private boolean cookieSecure;

    @RequestMapping(method = RequestMethod.GET, value = "/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        log.debug("CSRF 토큰 요청");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/refresh")
    public ResponseEntity<JwtDto> refresh(
            @CookieValue(name = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response) {
        JwtRefreshResultDto result = authService.refresh(refreshToken);

        ResponseCookie refreshCookie = ResponseCookie.from(
                        JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, result.refreshToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .sameSite("Lax")
                .maxAge(jwtTokenProvider.getRefreshTokenExpirationMinutes() * 60L)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(new JwtDto(result.userDto(), result.accessToken()));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/role")
    public ResponseEntity<UserDto> updateRole(@Valid @RequestBody UserRoleUpdateRequestDto request) {
        return ResponseEntity.ok(userService.updateRole(request));
    }
}
