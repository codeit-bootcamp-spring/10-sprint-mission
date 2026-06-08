package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
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

  public String generateAccessToken(Map<String, Object> claims, String subject) {
    try {
      JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));

      Date expiration = new Date(
          System.currentTimeMillis() + accessTokenExpirationMinutes * 60 * 1000);

      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
          .subject(subject)
          .claim("username", claims.get("username"))
          .claim("roles", claims.get("roles"))
          .claim("userId", claims.get("userId"))
          .expirationTime(expiration)
          .issueTime(new Date())
          .issuer("example.com")
          .build();

      SignedJWT signedJWT = new SignedJWT(
          new JWSHeader(JWSAlgorithm.HS256),
          claimsSet
      );

      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (Exception e) {
      throw new DiscodeitException(ErrorCode.INVALID_TOKEN);
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
          .issuer("example.com")
          .build();

      SignedJWT signedJWT = new SignedJWT(
          new JWSHeader(JWSAlgorithm.HS256),
          claimsSet
      );

      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (Exception e) {
      throw new DiscodeitException(ErrorCode.INVALID_TOKEN);
    }
  }

  public Map<String, Object> getClaims(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

      if (!signedJWT.verify(verifier)) {
        throw new DiscodeitException(ErrorCode.INVALID_TOKEN);
      }

      JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

      Date expirationTime = claimsSet.getExpirationTime();

      if (expirationTime.before(new Date())) {
        throw new DiscodeitException(ErrorCode.EXPIRED_TOKEN);
      }

      return claimsSet.getClaims();
    } catch (DiscodeitException e) {
      throw e;
    } catch (Exception e) {
      throw new DiscodeitException(ErrorCode.INVALID_TOKEN);
    }
  }
}
