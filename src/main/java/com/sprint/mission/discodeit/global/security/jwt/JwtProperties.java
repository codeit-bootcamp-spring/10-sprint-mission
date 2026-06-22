package com.sprint.mission.discodeit.global.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
    String secretKey,
    Token accessToken,
    RefreshToken refreshToken,
    Registry registry
) {
  public record Token(long expiration) {}

  public record RefreshToken(long expiration, CookieProperties cookie) {}

  public record CookieProperties(String name, String path, String sameSite, boolean secure) {}

  public record Registry(String storeType, Session session) {}

  public record Session(int maxConcurrent, int maxRetained) {}
}
