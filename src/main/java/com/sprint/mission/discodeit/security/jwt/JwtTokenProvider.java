package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.UserRole;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// JWT Access/Refresh Token의 생성/검증/파싱을 담당
// 서명 알고리즘으로 HMAC-SHA256(HS256)을 사용

// 생성 generateAccessToken / generateRefreshToken
// 검증 validateToken
// 파싱 extractUsername / extractUserDtoFromRefreshToken

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  // JWT Claims 키 상수
  private static final String CLAIM_USERNAME = "username";
  private static final String CLAIM_ROLE = "role";
  private static final String CLAIM_TYPE = "type";

  // 토큰 타입 값
  private static final String TYPE_ACCESS = "access";
  private static final String TYPE_REFRESH = "refresh";

  private final JwtProperties jwtProperties;

  // 사용자 정보 기반 AccessToken 생성
  public String generateAccessToken(UserDto userDto) {
    return buildToken(userDto, TYPE_ACCESS, jwtProperties.accessTokenExpiry());
  }

  // 사용자 정보 기반 RefreshToken 생성
  public String generateRefreshToken(UserDto userDto) {
    return buildToken(userDto, TYPE_REFRESH, jwtProperties.refreshTokenExpiry());
  }

  // JWT를 생성하고 HMAC-SHA256으로 서명한 뒤 직렬화된 문자열로 반환
  // generateAccessToken과 generateRefreshToken 둘 다 사용하는 중복 코드 분리
  private String buildToken(UserDto userDto, String type, long expiryMs) {
    try {
      Date now = new Date();
      JWTClaimsSet claims = new JWTClaimsSet.Builder()
          .subject(userDto.id().toString()) // sub - 토큰의 주체
          .claim(CLAIM_USERNAME, userDto.username()) // 커스텀 claims 추가
          .claim(CLAIM_ROLE, userDto.role().name())
          .claim(CLAIM_TYPE, type)
          .issueTime(now) // iat - 발급 시각
          .expirationTime(new Date(now.getTime() + expiryMs)) // exp - 만료 시각
          .build();
      byte[] secretBytes = jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8);
      // 아직 서명 안 된 객체 생성
      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
      // 서명 실행
      signedJWT.sign(new MACSigner(secretBytes));
      return signedJWT.serialize(); // 직렬화
    } catch (Exception e) {
      throw new IllegalStateException("토큰 생성 실패: " + e.getMessage(), e);
    }
  }

  // 토큰의 서명과 만료 여부를 검증
  public boolean validateToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token); // token -> SignedJWT 객체로 변환

      // 서명 검증
      // Header + Payload + 비밀키 -> HMAC 연산 -> 기존 Signature와 비교
      // 비밀키를 바이트 배열로 변환
      byte[] secretBytes = jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8);
      MACVerifier verifier = new MACVerifier(secretBytes); // 검증키 객체 생성
      // 토큰의 Header + Payload를 secretBytes로 HMAC-SHA256 재계산
      // 재계산한 값이 토큰의 Signature 부분과 같으면 true
      if (!signedJWT.verify(verifier)) {
        return false;
      }

      // 만료 시간 검증
      // exp 클레임을 Date 객체로 변환해서 반환
      Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
      // exp 클레임이 존재하고 만료시간이 현재 시각보다 이후이면 -> true
      return expiration != null && expiration.after(new Date());
    } catch (Exception e) {
      log.debug("토큰 검증 실패: {}", e.getMessage());
      return false;
    }
  }

  // JWT의 username 값 꺼내기
  public String extractUsername(String token) {
    try {
      return SignedJWT.parse(token).getJWTClaimsSet().getStringClaim(CLAIM_USERNAME);
    } catch (ParseException e) {
      throw new IllegalArgumentException("토큰 파싱 실패: " + e.getMessage());
    }
  }

  // 검증 + 파싱만 하고 UserDto 반환, 토큰 생성은 컨트롤러가 담당
  public UserDto extractUserDtoFromRefreshToken(String refreshToken) {
    if (!validateToken(refreshToken)) {
      throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
    }
    try {
      JWTClaimsSet claims = SignedJWT.parse(refreshToken).getJWTClaimsSet();
      if (!TYPE_REFRESH.equals(claims.getStringClaim(CLAIM_TYPE))) {
        throw new IllegalArgumentException("Refresh 토큰이 아닙니다.");
      }
      UUID userId = UUID.fromString(claims.getSubject());
      String username = claims.getStringClaim(CLAIM_USERNAME);
      UserRole role = UserRole.valueOf(claims.getStringClaim(CLAIM_ROLE));
      return new UserDto(userId, username, null, null, false, role);
    } catch (ParseException e) {
      throw new IllegalArgumentException("토큰 파싱 실패: " + e.getMessage());
    }
  }

  // 토큰 문자열을 파싱해 Claims를 추출
  private JWTClaimsSet parseClaims(String token) {
    try {
      return SignedJWT.parse(token).getJWTClaimsSet();
    } catch (ParseException e) {
      throw new IllegalArgumentException("토큰 파싱 실패: " + e.getMessage());
    }
  }
}
