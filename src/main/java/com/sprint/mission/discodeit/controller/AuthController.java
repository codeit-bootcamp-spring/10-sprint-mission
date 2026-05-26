package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.jwt.JwtDto;
import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
import com.sprint.mission.discodeit.dto.user.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Validated
@Tag(name = "Auth")
@Slf4j
public class AuthController {

  private final UserService userService;
  private final AuthService authService;

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtDto> refresh(
      @CookieValue(value = "REFRESH_TOKEN") String oldRefreshToken) {
    JwtInformation jwtInformation = authService.rotateToken(oldRefreshToken);
    ResponseCookie cookie = ResponseCookie.from(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME,
            jwtInformation.refreshToken())
        .httpOnly(true)
        .path("/")
        .maxAge(60 * 60 * 24 * 7)
        .build();
    JwtDto jwtDto = new JwtDto(jwtInformation.userDto(), jwtInformation.accessToken());
    log.debug("인증된 유저: userId={}", jwtInformation.userDto().id());
    return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(jwtDto);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/role")
  public ResponseEntity<UserDto> updateUserRole(@Valid @RequestBody RoleUpdateRequest request) {
    UserDto dto = userService.updateRole(request);
    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }
}
