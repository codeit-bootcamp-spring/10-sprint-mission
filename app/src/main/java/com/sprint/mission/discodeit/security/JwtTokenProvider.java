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
import com.sprint.mission.discodeit.dto.user.UserDto;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenProvider {

  public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  @Getter
  @Value("${discodeit.jwt.key}")
  private String secretKey;

  @Getter
  @Value("${discodeit.jwt.access-token-expiration-minutes}")
  private int accessTokenExpirationMinutes;

  @Getter
  @Value("${discodeit.jwt.refresh-token-expiration-minutes}")
  private int refreshTokenExpirationMinutes;

  private static final String ISSUER = "discodeit.com";
  private JWSSigner signer;
  private JWSVerifier verifier;

  @PostConstruct
  public void init() throws JOSEException {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    this.signer = new MACSigner(keyBytes);
    this.verifier = new MACVerifier(keyBytes);
  }

  private String createToken(String subject, Map<String, Object> claims, int expMinutes, String tokenType) {
    try {
      Date now = new Date();
      Date expiration = new Date(now.getTime() + expMinutes * 60 * 1000L);

      JWTClaimsSet baseClaimsSet = JWTClaimsSet.parse(claims);
      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder(baseClaimsSet)
          .subject(subject)
          .issuer(ISSUER)
          .issueTime(now)
          .expirationTime(expiration)
          .claim("type", tokenType)
          .build();

      SignedJWT signedJWT = new SignedJWT(
          new JWSHeader(JWSAlgorithm.HS256),
          claimsSet
      );
      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (Exception e) {
      throw new RuntimeException("[JWT] 토큰 발급 실패", e);
    }
  }

  public Map<String, Object> getClaims(String token) {
    try {
      SignedJWT signedJWT = verifyAndParse(token);

      return signedJWT.getJWTClaimsSet().getClaims();
    } catch (Exception e) {
      throw new RuntimeException("[JWT] JWT 파싱 실패", e);
    }
  }

  public String getSubject(String token) {
    try {
      SignedJWT signedJWT = verifyAndParse(token);

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

  public boolean validateAccessToken(String token) {
    return validateToken(token, "access");
  }

  public boolean validateRefreshToken(String token) {
    return validateToken(token, "refresh");
  }

  public boolean validateToken(String token, String expectedType) {
    try {
      // 1. 파싱
      SignedJWT signedJWT = SignedJWT.parse(token);

      // 2. 서명 검증 (데이터 변조 확인)
      if (!signedJWT.verify(verifier)) {
        log.warn("[JWT] 서명 검증 실패");
        return false;
      }

      // 3. 만료 시간 검증
      JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
      if (claims.getExpirationTime().before(new Date())) {
        log.warn("[JWT] 토큰 만료");
        return false;
      }

      // 4. 타입 검증 (Access vs Refresh 구분)
      String tokenType = claims.getStringClaim("type");
      if (!expectedType.equals(tokenType)) {
        log.warn("[JWT] 토큰 타입 불일치. 예상: {}, 실제: {}", expectedType, tokenType);
        return false;
      }

      return true;
    } catch (Exception e) {
      log.error("[JWT] 토큰 검증 중 에러 발생", e);
      return false;
    }
  }

  public String delegateAccessToken(UserDto userDto) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", userDto.username());
    claims.put("email", userDto.email());
    claims.put("roles", userDto.role().name());
    claims.put("userId", userDto.id());
    String subject = userDto.id().toString();
    return createToken(subject, claims, accessTokenExpirationMinutes, "access");
  }

  public String delegateRefreshToken(UserDto userDto) {
    String subject = userDto.id().toString();
    return createToken(subject, Map.of(), refreshTokenExpirationMinutes, "refresh");
  }

  private SignedJWT verifyAndParse(String token) throws Exception {
    SignedJWT signedJWT = SignedJWT.parse(token);
    JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));
    if (!signedJWT.verify(verifier)) {
      throw new RuntimeException("[JWT] JWT 검증 실패");
    }
    Date expiry = signedJWT.getJWTClaimsSet().getExpirationTime();
    if (expiry != null && expiry.before(new Date())) {
      throw new RuntimeException("[JWT] JWT 토큰이 만료되었습니다.");
    }
    return signedJWT;
  }
}
