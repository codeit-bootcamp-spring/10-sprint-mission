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
      throw new IllegalArgumentException("JWT 비밀키는 최소 32바이트(256비트) 이상이어야 합니다.");
    }

    this.signer = new MACSigner(keyBytes);
    this.verifier = new MACVerifier(keyBytes);
  }

  // Access Token 생성
  public String generateAccessToken(Map<String, Object> claims, String subject) {
    try {
      Date expiration = new Date(System.currentTimeMillis() + accessTokenExpirationMinutes * 60 * 1000L);
      JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder()
          .subject(subject)
          .expirationTime(expiration)
          .issueTime(new Date())
          .issuer("example.com");

      if (claims != null && claims.containsKey("roles")) {
          claimsSetBuilder.claim("roles", claims.get("roles"));
      }

      SignedJWT signedJWT = new SignedJWT(
          new JWSHeader(JWSAlgorithm.HS256),
          claimsSetBuilder.build()
      );

      signedJWT.sign(signer);
      return signedJWT.serialize();

    } catch (Exception e) {
      throw new RuntimeException("JWT 발급 실패", e);
    }
  }

  // Refresh Token 생성
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
      throw new RuntimeException("JWT Refresh Token 발급 실패", e);
    }
  }

  // 토큰 유효성 검증
  public boolean validateToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);

      // 서명 검증
      if (!signedJWT.verify(verifier)) {
        log.warn("JWT 서명 검증 실패: 위변조된 토큰일 가능성이 있습니다.");
        return false;
      }

      // 만료 시간 검증
      JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
      Date expirationTime = claimsSet.getExpirationTime();

      if (expirationTime == null || expirationTime.before(new Date())) {
        log.info("JWT 만료: 만료된 토큰입니다. (만료시간: {})", expirationTime);
        return false;
      }

      return true;


    } catch (Exception e) {
      // 파싱 실패 또는 검증 중 오류 발생 시 유효하지 않은 토큰으로 간주
      log.error("JWT 검증 중 예외 발생: {}", e.getMessage());
      return false;
    }
  }

  // 토큰에서 사용자 아이디(subject) 추출
  public String getUsername(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getSubject();
    } catch (Exception e) {
      throw new RuntimeException("JWT 파싱 실패", e);
    }
  }
}
