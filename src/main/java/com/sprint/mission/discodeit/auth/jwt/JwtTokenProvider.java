package com.sprint.mission.discodeit.auth.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.exception.auth.InvalidTokenException;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  public final static String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  @Getter
  @Value("${discodeit.jwt.secure}")
  private boolean secure;

  @Getter
  @Value("${discodeit.jwt.key}")
  private String secretKey;

  @Getter
  @Value("${discodeit.jwt.access-token-expiration-minutes}")
  private long accessTokenExpirationMinutes;

  @Getter
  @Value("${discodeit.jwt.refresh-token-expiration-minutes}")
  private long refreshTokenExpirationMinutes;

  @PostConstruct
  public void validateKeyLength() {
    if (secretKey == null || secretKey.getBytes(StandardCharsets.UTF_8).length < 32) {
      throw new IllegalArgumentException(
          "JWT Secret Key 길이는 최소 32바이트(256비트)이어야 합니다. currentKeyLength="
              + (secretKey == null ? 0 : secretKey.getBytes(StandardCharsets.UTF_8).length)
      );
    }
  }

  public String generateAccessToken(DiscodeitUserDetails userDetails) {
    return generateToken(TokenType.ACCESS, userDetails);
  }

  public String generateRefreshToken(DiscodeitUserDetails userDetails) {
    return generateToken(TokenType.REFRESH, userDetails);
  }

  private String generateToken(TokenType tokenType, DiscodeitUserDetails userDetails) {
    try {
      // KeyLengthException
      JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));
      JWTClaimsSet claimsSet = switch (tokenType) {
        case ACCESS -> new Builder()
            .subject(userDetails.getUsername())
            .issueTime(new Date())
            .expirationTime(
                new Date(System.currentTimeMillis() + accessTokenExpirationMinutes * 60 * 1000))
            .build();
        case REFRESH -> new Builder()
            .subject(userDetails.getUsername())
            .issueTime(new Date())
            .claim("userId", userDetails.getUserDto().id())
            .expirationTime(
                new Date(System.currentTimeMillis() + refreshTokenExpirationMinutes * 60 * 1000))
            .build();
      };
      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
      // JOSEException
      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (JOSEException e) {
      throw new RuntimeException("JWT 발급 실패", e);
    }
  }

  enum TokenType {
    ACCESS,
    REFRESH
  }

  public String getUserId(String token) {
    return verifyAndGetClaims(token).get("userId").toString();
  }

  public boolean validateToken(String token) {
    try {
      verifyAndGetClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public Map<String, Object> verifyAndGetClaims(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

      if (!signedJWT.verify(verifier)) {
        log.warn("JWT 서명 검증 실패: 유효하지 않은 서명");
        throw new InvalidTokenException();
      }

      JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
      if (claimsSet.getExpirationTime() != null &&
          claimsSet.getExpirationTime().before(new Date())) {
        log.warn("JWT 검증 실패: 만료된 토큰");
        throw new InvalidTokenException();
      }

      return claimsSet.getClaims();
    } catch (ParseException | JOSEException e) {
      log.warn("JWT 파싱 실패: 잘못된 형식의 토큰 details={}", e.getMessage());
      throw new InvalidTokenException();
    }
  }

  public ResponseCookie generateRefreshTokenCookie(String refreshToken) {
    return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
        .path("/")
        .httpOnly(true)
        .secure(secure)
        .sameSite("Lax")
        .maxAge(getRefreshTokenExpirationMinutes() * 60)
        .build();
  }

  public ResponseCookie generateRefreshTokenCookieExpiration() {
    return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME)
        .path("/")
        .httpOnly(true)
        .secure(secure)
        .sameSite("Lax")
        .maxAge(0)
        .build();
  }
}
