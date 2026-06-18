package com.sprint.mission.discodeit.auth.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.auth.jwt.dto.AccessTokenClaims;
import jakarta.servlet.http.Cookie;
import java.text.ParseException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  @Value("${jwt.secrete-key}")
  private String SECRET_KEY;

  @Value("${jwt.issuer}")
  private String ISSUER;

  @Value("${jwt.access-token-validity-seconds}")
  private int ACCESS_TOKEN_VALIDITY_SECONDS;

  @Value("${jwt.refresh-token-validity-seconds}")
  private int REFRESH_TOKEN_VALIDITY_SECONDS;

  public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  public String generateAccessToken(String subject, AccessTokenClaims claims) {
    Date expirationDate = new Date(
        System.currentTimeMillis() + (ACCESS_TOKEN_VALIDITY_SECONDS * 1000L));

    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
        .subject(subject)
        .claim("role", claims.role())
        .issuer(ISSUER)
        .issueTime(new Date())
        .expirationTime(expirationDate)
        .build();

    log.debug("액세스 키 생성: userId={}", subject);
    return createSignedJWT(jwtClaimsSet).serialize();
  }

  public String generateRefreshToken(String subject) {
    Date expirationDate = new Date(
        System.currentTimeMillis() + (REFRESH_TOKEN_VALIDITY_SECONDS * 1000L));

    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
        .subject(subject)
        .issuer(ISSUER)
        .issueTime(new Date())
        .expirationTime(expirationDate)
        .build();

    log.debug("리프레시 토큰 생성: userId={}", subject);
    return createSignedJWT(jwtClaimsSet).serialize();
  }

  public boolean validateToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWSVerifier verifier = new MACVerifier(SECRET_KEY);
      if (!signedJWT.verify(verifier)) {
        log.warn("JWT 서명 불일치");
        return false;
      }
      Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
      if (expirationDate.before(new Date())) {
        log.warn("JWT 토큰 만료");
        return false;
      }

      return true;

    } catch (ParseException | JOSEException e) {
      log.error("JWT 토큰 검증 중 예외 발생: {}", e.getMessage());
      return false;
    }
  }

  public String getSubjectFromToken(String token) {
    JWTClaimsSet claimsSet = getClaimsFromToken(token);
    return claimsSet.getSubject();
  }

  public Cookie getRefreshTokenCookie(String refreshToken) {
    return getRefreshTokenCookie(refreshToken, REFRESH_TOKEN_VALIDITY_SECONDS);
  }

  public Cookie getRefreshTokenCookie(String refreshToken, int maxAge) {
    Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setAttribute("SameSite", "Lax");
    refreshTokenCookie.setMaxAge(maxAge);
    return refreshTokenCookie;
  }
  
  private JWTClaimsSet getClaimsFromToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
      return claimsSet;
    } catch (ParseException e) {
      log.error("JWT 토큰 파싱 실패: {}", e.getMessage());
      throw new IllegalArgumentException("유효하지 않은 토큰 형식", e);
    }
  }

  private SignedJWT createSignedJWT(JWTClaimsSet jwtClaimsSet) {
    SignedJWT signedJWT = new SignedJWT(
        new JWSHeader(JWSAlgorithm.HS256),
        jwtClaimsSet
    );

    try {
      JWSSigner jwsSigner = new MACSigner(SECRET_KEY);
      signedJWT.sign(jwsSigner);
      return signedJWT;
    } catch (JOSEException e) {
      log.error("JWT 서명 실패: {}", e.getMessage());
      throw new RuntimeException("사인 토큰 생성 실패", e);
    }
  }

}
