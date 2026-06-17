package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  @Getter
  @Value("${jwt.key}")
  private String secretKey;

  @Getter
  @Value("${jwt.access-token-expiration-minutes}")
  private int accessTokenExpirationMinutes;

  @Getter
  @Value("${jwt.refresh-token-expiration-minutes}")
  private int refreshTokenExpirationMinutes;

  // access 토큰 생성
  public String generateAccessToken(Map<String, Object> claims, String subject) {
    try {
      JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));

      Date expiration = new Date(
          System.currentTimeMillis() + accessTokenExpirationMinutes * 60 * 1000L);

      JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
          .subject(subject)
          .expirationTime(expiration)
          .issueTime(new Date())
          .issuer("discodeit");

      claims.forEach(builder::claim);

      JWTClaimsSet claimsSet = builder.build();

      SignedJWT signedJWT = new SignedJWT(
          new JWSHeader(JWSAlgorithm.HS256),
          claimsSet
      );

      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (Exception e) {
      throw new RuntimeException("JWT 발급 실패", e);
    }
  }

  // refresh 토큰 생성
  public String generateRefreshToken(String subject) {
    try {
      JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));

      Date expiration = new Date(
          System.currentTimeMillis() + refreshTokenExpirationMinutes * 60 * 1000L);

      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
          .subject(subject)
          .expirationTime(expiration)
          .issueTime(new Date())
          .issuer("discodeit")
          .build();

      SignedJWT signedJWT = new SignedJWT(
          new JWSHeader(JWSAlgorithm.HS256),
          claimsSet
      );

      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (Exception e) {
      throw new RuntimeException("JWT 발급 실패", e);
    }
  }

  // token 검증
  public Map<String, Object> getClaims(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

      if (!signedJWT.verify(verifier)) {
        throw new RuntimeException("JWT 검증 실패");
      }

      JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

      if (claimsSet.getExpirationTime().before(new Date())) {
        throw new RuntimeException("JWT 만료");
      }

      return claimsSet.getClaims();

    } catch (Exception e) {
      throw new RuntimeException("JWT 파싱 실패", e);
    }
  }

  // 만료시간
  public Instant getExpiration(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT
          .getJWTClaimsSet()
          .getExpirationTime()
          .toInstant();
    } catch (Exception e) {
      throw new RuntimeException("JWT 만료 시간 추출 실패", e);
    }
  }
}

