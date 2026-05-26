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
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.auth.AuthException;
import com.sprint.mission.discodeit.exception.auth.TokenGenerationException;
import com.sprint.mission.discodeit.exception.auth.TokenParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  private final JWSSigner signer;
  private final JWSVerifier verifier;
  private final long accessExpirationMillis;
  private final long refreshExpirationMillis;

  // nimbus-jose-jwt를 활용한 토큰 발급/ 갱신/ 검증 컴포넌트
  // 생성자 주입 시점에 Secret Key를 기반으로 Signer와 Verifier를 미리 초기화하여 성능 최적화
  public JwtTokenProvider(
      @Value("${jwt.secret}") String secretKey,
      @Value("${jwt.access-expiration}") long accessExpirationMillis,
      @Value("${jwt.refresh-expiration}") long refreshExpirationMillis) throws JOSEException {
    this.signer = new MACSigner(secretKey.getBytes());
    this.verifier = new MACVerifier(secretKey.getBytes());
    this.accessExpirationMillis = accessExpirationMillis;
    this.refreshExpirationMillis = refreshExpirationMillis;
  }

  // 로그인 성공 핸들러(JwtLoginSuccessHandler) 및 토큰 재발급 API에서 엑세스 토큰을 만들 때 활용
  public String createAccessToken(DiscodeitUserDetails userDetails) {
    return createToken(userDetails.getId().toString(), accessExpirationMillis);
  }

  // 로그인 성공 핸들러 및 토큰 로테이션 시 리프레시 토큰을 만들고 쿠키에 담을 때 활용
  public String createRefreshToken(DiscodeitUserDetails userDetails) {
    return createToken(userDetails.getId().toString(), refreshExpirationMillis);
  }

  private String createToken(String subject, long expirationMillis) {
    Instant now = Instant.now();
    Instant expirationTime = now.plusMillis(expirationMillis);

    try {
      JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
          .subject(subject)
          // Nimbus 내부 API 스펙을 위해 Date로 변환
          .issueTime(Date.from(now))
          .expirationTime(Date.from(expirationTime))
          .jwtID(UUID.randomUUID().toString())
          .build();

      SignedJWT signedJWT = new SignedJWT(header, claimsSet);
      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (JOSEException e) {
      log.error("Failed to sign and serialize JWT token for subject: {}", subject, e);
      throw new TokenGenerationException(Map.of("reason", "토큰 서명 암호화에 실패했습니다."), e);
    }
  }

  // 들어온 토큰이 유효한지 확인
  public boolean validateToken(String token) {
    try { // 검증되었는지 확인
      SignedJWT signedJWT = SignedJWT.parse(token);
      if (!signedJWT.verify(verifier)) {
        log.debug("JWT signature verification failed");
        return false; // 필터 단은 글로벌 핸들러가 처리 못 하니 false 반환 -> 401
      }
      // 만료 여부 확인
      Instant expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime().toInstant();
      if (!expirationTime.isAfter(Instant.now())) {
        log.debug("JWT token is expired");
        return false; // 필터 단은 글로벌 핸들러가 처리 못 하니 false 반환 -> 401
      }
      return true;
    } catch (Exception e) {
      log.debug("Invalid or malformed JWT token provided", e);
      return false; // 필터 단은 글로벌 핸들러가 처리 못 하니 false 반환 -> 401
    }
  }

  // 토큰에서 Subject (userId) 추출
  public String getSubjectFromToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getSubject();
    } catch (Exception e) {
      log.error("Failed to parse subject from JWT token", e);
      throw new TokenParseException(Map.of("reason", "토큰 식별자(Subject) 파싱에 실패했습니다."), e);
    }
  }

  // 토큰에서 만료시간 추출
  public Instant getExpirationFromToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getExpirationTime().toInstant();
    } catch (Exception e) {
      log.error("Failed to parse expiration from JWT token", e);
      throw new TokenParseException(Map.of("reason", "토큰 만료시간 파싱에 실패했습니다."), e);
    }
  }
}
