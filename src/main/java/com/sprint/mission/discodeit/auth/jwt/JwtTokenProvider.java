package com.sprint.mission.discodeit.auth.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰의 생성 및 유효성 검증을 담당하는 컴포넌트입니다.
 * Nimbus JOSE + JWT 라이브러리를 사용합니다.
 */
@Slf4j
@Component
public class JwtTokenProvider {

  public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  @Value("${jwt.key}")
  private String secretKey;

  @Value("${jwt.access-token-expiration-minutes}")
  private int accessTokenExpirationMinutes;

  @Value("${jwt.refresh-token-expiration-minutes}")
  private int refreshTokenExpirationMinutes;

  private JWSSigner signer;
  private JWSVerifier verifier;

  @PostConstruct
  public void init() throws Exception {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

    if (keyBytes.length < 32) {
      throw new IllegalArgumentException("JWT 비밀키는 최소 32바이트 이상이어야 합니다. (현재 길이: " + keyBytes.length + ")");
    }

    this.signer = new MACSigner(keyBytes);
    this.verifier = new MACVerifier(keyBytes);
  }

  /**
   * 액세스 토큰 생성 (사용자 ID 및 권한 포함)
   */
  public String generateAccessToken(Map<String, Object> claims, String subject) {
    try {
      Date expiration = new Date(System.currentTimeMillis() + accessTokenExpirationMinutes * 60 * 1000L);
      JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder()
          .subject(subject)
          .expirationTime(expiration)
          .issueTime(new Date())
          .issuer("discodeit.com");

      if (claims != null && claims.containsKey("roles")) {
          claimsSetBuilder.claim("roles", claims.get("roles"));
      }

      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSetBuilder.build());
      signedJWT.sign(signer);
      return signedJWT.serialize();

    } catch (Exception e) {
      log.error("Access Token 생성 중 오류 발생: {}", e.getMessage());
      throw new RuntimeException("토큰 생성 실패", e);
    }
  }

  /**
   * 리프레시 토큰 생성 (Subject만 포함)
   */
  public String generateRefreshToken(String subject) {
    try {
      Date now = new Date();
      Date expiration = new Date(now.getTime() + refreshTokenExpirationMinutes * 60 * 1000L);

      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
          .subject(subject)
          .issueTime(now)
          .expirationTime(expiration)
          .build();

      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
      signedJWT.sign(signer);
      return signedJWT.serialize();

    } catch (Exception e) {
      log.error("Refresh Token 생성 중 오류 발생: {}", e.getMessage());
      throw new RuntimeException("리프레시 토큰 생성 실패", e);
    }
  }

  /**
   * 토큰 유효성 및 만료 여부 확인
   */
  public boolean validateToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);

      if (!signedJWT.verify(verifier)) {
        log.warn("JWT 서명 검증 실패");
        return false;
      }

      Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
      if (expirationTime == null || expirationTime.before(new Date())) {
        log.debug("JWT 만료됨: {}", expirationTime);
        return false;
      }

      return true;

    } catch (Exception e) {
      log.debug("JWT 검증 실패: {}", e.getMessage());
      return false;
    }
  }

  /**
   * 토큰에서 사용자 ID(Subject) 추출
   */
  public String getUsername(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getSubject();
    } catch (Exception e) {
      log.error("JWT 파싱 실패: {}", e.getMessage());
      throw new RuntimeException("토큰 해석 실패", e);
    }
  }
}
