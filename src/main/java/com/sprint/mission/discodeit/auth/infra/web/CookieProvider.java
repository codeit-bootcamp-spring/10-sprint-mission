package com.sprint.mission.discodeit.auth.infra.web;

import com.sprint.mission.discodeit.global.security.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieProvider {

  private final JwtProperties jwtProperties;

  public ResponseCookie createRefreshTokenCookie(String token) {
    JwtProperties.RefreshToken refreshProps = jwtProperties.refreshToken();
    JwtProperties.CookieProperties cookieProps = refreshProps.cookie();
    return ResponseCookie.from(cookieProps.name(), token)
        .path(cookieProps.path())
        .maxAge(refreshProps.expiration())
        .httpOnly(true)
        .secure(cookieProps.secure())
        .sameSite(cookieProps.sameSite())
        .build();
  }

  public ResponseCookie createDeleteCookie() {
    JwtProperties.CookieProperties cookieProps = jwtProperties.refreshToken().cookie();
    return ResponseCookie.from(cookieProps.name(), "")
        .path(cookieProps.path())
        .maxAge(0)
        .httpOnly(true)
        .secure(cookieProps.secure())
        .sameSite(cookieProps.sameSite())
        .build();
  }

  public String getRefreshTokenName() {
    return jwtProperties.refreshToken().cookie().name();
  }
}
