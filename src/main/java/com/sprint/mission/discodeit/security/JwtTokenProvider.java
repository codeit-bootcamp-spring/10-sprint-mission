package com.sprint.mission.discodeit.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  public static String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  @Getter
  @Value("${discodeit.jwt.key}")
  private String secretKey;

  @Getter
  @Value("${discodeit.jwt.access-token-expiration-minutes}")
  private int accessTokenExpirationMinutes;

  @Getter
  @Value("${discodeit.jwt.refresh-token-expiration-minutes}")
  private int refreshTokenExpirationMinutes;

  public String generateAccessToken(Map<String, Object> claims, String subject) {
    try {
      JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));
      Date expiration = new Date(
          System.currentTimeMillis() + accessTokenExpirationMinutes * 60 * 1000);
      JWTClaimsSet baseClaimsSet = JWTClaimsSet.parse(claims);
      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder(baseClaimsSet)
          .subject(subject)
          .expirationTime(expiration)
          .issueTime(new Date())
          .issuer("discodeit.com")
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

  public String generateRefreshToken(String subject) {
    try {
      JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));

      Date expiration = new Date(
          System.currentTimeMillis() + refreshTokenExpirationMinutes * 60 * 1000);

      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
          .subject(subject)
          .expirationTime(expiration)
          .issueTime(new Date())
          .issuer("discodeit.com")
          .build();

      SignedJWT signedJWT = new SignedJWT(
          new JWSHeader(JWSAlgorithm.HS256),
          claimsSet
      );

      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (Exception e) {
      throw new RuntimeException("[JWT] JWT 발급 실패", e);
    }
  }

  public Map<String, Object> getClaims(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

      if (!signedJWT.verify(verifier)) {
        throw new RuntimeException("[JWT] JWT 검증 실패");
      }

      Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
      if (expirationTime != null && expirationTime.before(new Date())) {
        throw new RuntimeException("[JWT] JWT 토큰이 만료되었습니다.");
      }

      JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
      return claimsSet.getClaims();
    } catch (Exception e) {
      throw new RuntimeException("[JWT] JWT 파싱 실패", e);
    }
  }

  public String getSubject(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

      if (!signedJWT.verify(verifier)) {
        throw new RuntimeException("[JWT] JWT 검증 실패");
      }

      Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
      if (expirationTime != null && expirationTime.before(new Date())) {
        throw new RuntimeException("[JWT] JWT 토큰이 만료되었습니다.");
      }

      return signedJWT.getJWTClaimsSet().getSubject();
    } catch (Exception e) {
      throw new RuntimeException("[JWT] JWT 파싱 실패", e);
    }
  }

  public boolean isExpired(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
      return expirationTime == null || expirationTime.before(new Date());
    } catch (Exception e) {
      // 파싱 실패 시에도 유효하지 않음으로 삭제
      return true;
    }
  }
}
