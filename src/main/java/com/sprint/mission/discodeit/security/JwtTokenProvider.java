package com.sprint.mission.discodeit.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.entity.Role;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
  private final String issuer;
  private final String secretKey;
  private final long accessTokenExpirationSeconds;
  private final long refreshTokenExpirationSeconds;

  public JwtTokenProvider(
      @Value("${discodeit.jwt.issuer}") String issuer,
      @Value("${discodeit.jwt.secret}") String secretKey,
      @Value("${discodeit.jwt.access-token-expiration-seconds}") long accessTokenExpirationSeconds,
      @Value("${discodeit.jwt.refresh-token-expiration-seconds}")
          long refreshTokenExpirationSeconds) {
    this.issuer = issuer;
    this.secretKey = secretKey;
    this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
    this.refreshTokenExpirationSeconds = refreshTokenExpirationSeconds;
  }

  // Access Token 발급
  public String generateAccessToken(UUID userId, String username, Role role) {
    Instant now = Instant.now();
    Instant expiresAt = now.plusSeconds(accessTokenExpirationSeconds);

    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .issuer(issuer)
            .subject(userId.toString())
            .claim("username", username)
            .claim("role", role.name())
            .issueTime(Date.from(now))
            .expirationTime(Date.from(expiresAt))
            .build();

    return sign(claimsSet);
  }

  // Refresh Token 발급
  public String generateRefreshToken(UUID userId) {
    Instant now = Instant.now();
    Instant expiresAt = now.plusSeconds(refreshTokenExpirationSeconds);

    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .issuer(issuer)
            .subject(userId.toString())
            .issueTime(Date.from(now))
            .expirationTime(Date.from(expiresAt))
            .build();

    return sign(claimsSet);
  }

  // Refresh token 유효 -> 새로운 Access Token 발급
  public String refreshAccessToken(String refreshToken, String username, Role role) {
    JWTClaimsSet claimsSet = parseAndValidate(refreshToken);
    UUID userId = UUID.fromString(claimsSet.getSubject());

    return generateAccessToken(userId, username, role);
  }

  // 토큰이 유효한지 확인
  public boolean validateToken(String token) {
    try {
      parseAndValidate(token);
      return true;
    } catch (RuntimeException e) {
      return false;
    }
  }

  public Map<String, Object> getClaims(String token) {
    return parseAndValidate(token).getClaims();
  }

  public UUID getUserId(String token) {
    JWTClaimsSet claimsSet = parseAndValidate(token);
    return UUID.fromString(claimsSet.getSubject());
  }

  public String getUsername(String token) {
    JWTClaimsSet claimsSet = parseAndValidate(token);
    return getStringClaim(claimsSet, "username");
  }

  public Role getRole(String token) {
    JWTClaimsSet claimsSet = parseAndValidate(token);
    return Role.valueOf(getStringClaim(claimsSet, "role"));
  }

  // JWT 문자열을 파싱 후 유효성을 검증한 뒤 ClaimsSet을 반환

  private JWTClaimsSet parseAndValidate(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);

      JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));
      if (!signedJWT.verify(verifier)) {
        throw new IllegalArgumentException("JWT 서명이 유효하지 않습니다.");
      }

      JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

      if (!issuer.equals(claimsSet.getIssuer())) {
        throw new IllegalArgumentException("JWT issuer가 유효하지 않습니다.");
      }

      Date expirationTime = claimsSet.getExpirationTime();
      if (expirationTime == null || expirationTime.before(new Date())) {
        throw new IllegalArgumentException("JWT 토큰이 만료되었습니다.");
      }

      return claimsSet;
    } catch (ParseException | JOSEException e) {
      throw new IllegalArgumentException("JWT 토큰이 유효하지 않습니다.", e);
    }
  }

  private String getStringClaim(JWTClaimsSet claimsSet, String claimName) {
    Object value = claimsSet.getClaim(claimName);

    if (!(value instanceof String stringValue)) {
      throw new IllegalArgumentException("JWT claim이 유효하지 않습니다: " + claimName);
    }

    return stringValue;
  }

  // ClaimsSet을 서명후 JWT 문자열로 변환
  private String sign(JWTClaimsSet claimsSet) {
    try {
      JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));

      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (JOSEException e) {
      throw new IllegalStateException("JWT 토큰 발급에 실패했습니다.", e);
    }
  }
}
