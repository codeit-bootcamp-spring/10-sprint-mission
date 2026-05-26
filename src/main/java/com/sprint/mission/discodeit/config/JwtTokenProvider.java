package com.sprint.mission.discodeit.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.DiscodeitUserDetails;
import com.sprint.mission.discodeit.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenProvider {

  private static final String TOKEN_TYPE_CLAIM = "typ"; // 토큰 타입
  private static final String USER_ID_CLAIM = "uid"; // 유저 아이디
  private static final String USERNAME_CLAIM = "username"; // 유저 이름
  private static final String EMAIL_CLAIM = "email";
  private static final String ROLE_CLAIM = "role"; // 권한
  private static final String ACCESS_TOKEN_TYPE = "access"; // 액세스 토큰 타입
  private static final String REFRESH_TOKEN_TYPE = "refresh"; // Refresh 토큰 타입
  private static final String BEARER_PREFIX = "Bearer "; // Bearer 헤더 Prefix

  private final byte[] secretKey; // 비밀 키
  private final Duration accessTokenValidity; // 액세스 토큰의 유효기간
  private final Duration refreshTokenValidity; // Refresh 토큰의 유효기간

  public JwtTokenProvider(
      @Value("${discodeit.jwt.secret:discodeit-local-development-jwt-secret}") String secret,
      @Value("${discodeit.jwt.access-token-validity-seconds:1800}") long accessTokenValiditySeconds,
      @Value("${discodeit.jwt.refresh-token-validity-seconds:1209600}") long refreshTokenValiditySeconds
  ) {
    this.secretKey = sha256(secret); // sha256 알고리즘으로 secret키를 암호화
    this.accessTokenValidity = Duration.ofSeconds(accessTokenValiditySeconds);
    this.refreshTokenValidity = Duration.ofSeconds(refreshTokenValiditySeconds);
  }

  // authentication을 받아 액세스 토큰을 생성
  public String generateAccessToken(Authentication authentication) {
    return createToken(extractUserDto(authentication), ACCESS_TOKEN_TYPE, accessTokenValidity);
  }

  // authentication을 받아 Refresh 토큰을 생성
  public String generateRefreshToken(Authentication authentication) {
    return createToken(extractUserDto(authentication), REFRESH_TOKEN_TYPE, refreshTokenValidity);
  }

  public String refreshAccessToken(String refreshToken) {
    JWTClaimsSet claims = parseAndValidate(refreshToken, REFRESH_TOKEN_TYPE);
    return createToken(toUserDto(claims), ACCESS_TOKEN_TYPE, accessTokenValidity);
  }

  public boolean validateAccessToken(String token) {
    return isValid(token, ACCESS_TOKEN_TYPE);
  }

  public boolean validateRefreshToken(String token) {
    return isValid(token, REFRESH_TOKEN_TYPE);
  }

  public Authentication getAuthentication(String accessToken) {
    JWTClaimsSet claims = parseAndValidate(accessToken, ACCESS_TOKEN_TYPE);
    DiscodeitUserDetails principal = new DiscodeitUserDetails(toUserDto(claims), "");
    return new UsernamePasswordAuthenticationToken(
        principal,
        accessToken,
        principal.getAuthorities()
    );
  }

  public String resolveToken(HttpServletRequest request) {
    String authorization = request.getHeader("Authorization");
    if (StringUtils.hasText(authorization) && authorization.startsWith(BEARER_PREFIX)) {
      return authorization.substring(BEARER_PREFIX.length());
    }
    return null;
  }

  // 토큰을 실질적으로 생성하는 ㅔㅁ서드
  private String createToken(UserDto userDto, String tokenType, Duration validity) {
    Objects.requireNonNull(userDto, "userDto must not be null");

    Instant issuedAt = Instant.now(); // iat = 지금
    Instant expiresAt = issuedAt.plus(validity); // 만료 시간 설정

    // 토큰의 claim들을 빌드한다.
    JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .subject(userDto.id().toString())
        .issueTime(Date.from(issuedAt))
        .expirationTime(Date.from(expiresAt))
        .claim(TOKEN_TYPE_CLAIM, tokenType)
        .claim(USER_ID_CLAIM, userDto.id().toString())
        .claim(USERNAME_CLAIM, userDto.username())
        .claim(EMAIL_CLAIM, userDto.email())
        .claim(ROLE_CLAIM, userDto.role().name())
        .build();

    // 이건 머죠
    SignedJWT signedJWT = new SignedJWT(
        new JWSHeader.Builder(JWSAlgorithm.HS256).build(),
        claims
    );

    try {
      signedJWT.sign(new MACSigner(secretKey));
      return signedJWT.serialize();
    } catch (JOSEException e) {
      throw new IllegalStateException("Failed to issue JWT", e);
    }
  }

  private boolean isValid(String token, String expectedType) {
    try {
      parseAndValidate(token, expectedType);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private JWTClaimsSet parseAndValidate(String token, String expectedType) {
    if (!StringUtils.hasText(token)) {
      throw new IllegalArgumentException("Token is empty");
    }

    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

      if (!signedJWT.verify(new MACVerifier(secretKey))) {
        throw new IllegalArgumentException("Invalid token signature");
      }
      if (claims.getExpirationTime() == null
          || !claims.getExpirationTime().toInstant().isAfter(Instant.now())) {
        throw new IllegalArgumentException("Token expired");
      }
      if (!expectedType.equals(claims.getStringClaim(TOKEN_TYPE_CLAIM))) {
        throw new IllegalArgumentException("Unexpected token type");
      }
      if (!hasRequiredClaims(claims)) {
        throw new IllegalArgumentException("Required token claims are missing");
      }

      return claims;
    } catch (ParseException | JOSEException e) {
      throw new IllegalArgumentException("Invalid token", e);
    }
  }

  private boolean hasRequiredClaims(JWTClaimsSet claims) throws ParseException {
    return StringUtils.hasText(claims.getStringClaim(USER_ID_CLAIM))
        && StringUtils.hasText(claims.getStringClaim(USERNAME_CLAIM))
        && StringUtils.hasText(claims.getStringClaim(ROLE_CLAIM));
  }

  private UserDto toUserDto(JWTClaimsSet claims) {
    try {
      return new UserDto(
          UUID.fromString(claims.getStringClaim(USER_ID_CLAIM)),
          claims.getStringClaim(USERNAME_CLAIM),
          claims.getStringClaim(EMAIL_CLAIM),
          null,
          true,
          Role.valueOf(claims.getStringClaim(ROLE_CLAIM))
      );
    } catch (ParseException e) {
      throw new IllegalArgumentException("Invalid token claims", e);
    }
  }

  private UserDto extractUserDto(Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (principal instanceof DiscodeitUserDetails userDetails) {
      return userDetails.getUserDto();
    }
    throw new IllegalArgumentException("Unsupported authentication principal");
  }

  private static byte[] sha256(String secret) {
    try {
      return MessageDigest.getInstance("SHA-256")
          .digest(secret.getBytes(StandardCharsets.UTF_8));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 is not available", e);
    }
  }
}
