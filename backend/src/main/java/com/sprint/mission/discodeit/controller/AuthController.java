package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.Cookie;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private static final String REFRESH_TOKEN_COOKIE_NAME =
      "REFRESH_TOKEN";

  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService userDetailsService;
  private final com.sprint.mission.discodeit.service.AuthService authService;

  private final UserService userService;

  @Override
  @PutMapping("/role")
  public ResponseEntity<UserDto> updateUserRole(@RequestBody UserRoleUpdateRequest request) {
    return ResponseEntity.ok(userService.updateRole(request));
  }

  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청");
    log.trace("CSRF 토큰: {}", csrfToken.getToken());
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @PostMapping("refresh")
  public ResponseEntity<JwtDto> refresh(@CookieValue("REFRESH_TOKEN") String refreshToken,
      HttpServletResponse response) {
    log.info("토큰 리프레시 요청");
    JwtInformation jwtInformation = authService.refreshToken(refreshToken);
    Cookie refreshCookie = jwtTokenProvider.genereateRefreshTokenCookie(
        jwtInformation.getRefreshToken());
    response.addCookie(refreshCookie);

    JwtDto body = new JwtDto(
        jwtInformation.getUserDto(),
        jwtInformation.getAccessToken()
    );
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(body);
  }

}
