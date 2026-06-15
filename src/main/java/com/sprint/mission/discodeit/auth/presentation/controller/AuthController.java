package com.sprint.mission.discodeit.auth.presentation.controller;

import com.sprint.mission.discodeit.auth.presentation.dto.JwtLoginResponse;
import com.sprint.mission.discodeit.auth.application.service.JwtTokenService;
import com.sprint.mission.discodeit.auth.infra.web.CookieProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final JwtTokenService jwtTokenService;
  private final CookieProvider cookieProvider;

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> csrfToken(CsrfToken token) {
    token.getToken();
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtLoginResponse> refresh(
      @CookieValue(value = "REFRESH_TOKEN", required = false) String token) {
    if (token == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    JwtLoginResponse response = jwtTokenService.reissue(token);
    ResponseCookie cookie = cookieProvider.createRefreshTokenCookie(response.refreshToken());
    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(response);
  }
}
