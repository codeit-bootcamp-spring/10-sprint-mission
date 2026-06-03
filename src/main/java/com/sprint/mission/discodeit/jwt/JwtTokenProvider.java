package com.sprint.mission.discodeit.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.common.exception.auth.TokenInvalidException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  @Getter
  @Value("${jwt.key}")
  private String secretKey;

  @Getter
  @Value("${jwt.access-token-expiration}")
  private int accessTokenExpiration;

  @Getter
  @Value("${jwt.refresh-token-expiration}")
  private int refreshTokenExpiration;

  public String generateToken(Map<String, Object> claims, String subject) {
    try {
      JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));

      Date expiration = new Date(System.currentTimeMillis() + accessTokenExpiration * 60 * 1000L);

      JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
          .subject(subject)
          .expirationTime(expiration)
          .issueTime(new Date());

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

  public String generateRefreshToken(String subject) {
    try {
      JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));

      Date expiration = new Date(System.currentTimeMillis() + refreshTokenExpiration * 60 * 1000L);

      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
          .subject(subject)
          .expirationTime(expiration)
          .issueTime(new Date())
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

  public Map<String, Object> getClaims(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);

      JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

      if (!signedJWT.verify(verifier)) {
        throw new TokenInvalidException();
      }

      Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
      if (expiration == null || expiration.before(new Date())) {
        throw new TokenInvalidException();
      }

      return signedJWT.getJWTClaimsSet().getClaims();

    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("JWT 파싱 실패", e);
    }
  }

}
