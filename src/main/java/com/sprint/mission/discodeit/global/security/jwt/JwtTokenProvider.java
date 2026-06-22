package com.sprint.mission.discodeit.global.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.global.security.exception.JwtAuthenticationErrorCode;
import com.sprint.mission.discodeit.global.security.exception.JwtAuthenticationException;
import com.sprint.mission.discodeit.global.security.utils.DiscodeitAuthorityUtils;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class JwtTokenProvider {

  private static final String AUTHORITIES_KEY = "role";
  private final JwtProperties jwtProperties;
  private final JWSSigner signer;
  private final JWSVerifier verifier;
  private final JWSHeader header;

  public JwtTokenProvider(JwtProperties jwtProperties) throws JOSEException {
    this.jwtProperties = jwtProperties;
    byte[] secretBytes = jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8);
    this.signer = new MACSigner(secretBytes);
    this.verifier = new MACVerifier(secretBytes);
    this.header = new JWSHeader(JWSAlgorithm.HS256);
  }

  public String generateAccessToken(Object subject, Object authorities) {
    return buildToken(subject.toString(), authorities.toString(), getAccessTokenExpiration());
  }

  public String generateRefreshToken(Object subject) {
    return buildToken(subject.toString(), null, getRefreshTokenExpiration());
  }

  public Authentication getAuthentication(String token) {
    SignedJWT signedJWT = getOrThrow(() -> SignedJWT.parse(token),
        new JwtAuthenticationException(JwtAuthenticationErrorCode.TOKEN_CANT_BE_PARSED));

    JWTClaimsSet claims = getOrThrow(signedJWT::getJWTClaimsSet,
        new JwtAuthenticationException(JwtAuthenticationErrorCode.PAYLOAD_CANT_BE_PARSED));

    String authClaim = getOrThrow(() -> claims.getStringClaim(AUTHORITIES_KEY),
        new JwtAuthenticationException(JwtAuthenticationErrorCode.ROLE_NOT_FOUND));

    if (!StringUtils.hasText(authClaim)) {
      throw new JwtAuthenticationException(JwtAuthenticationErrorCode.ROLE_NOT_FOUND);
    }

    Collection<? extends GrantedAuthority> authorities =
        DiscodeitAuthorityUtils.deserialize(authClaim);
    UserDetails principal = new User(claims.getSubject(), "", authorities);
    return new UsernamePasswordAuthenticationToken(principal, token, authorities);
  }

  public void validate(String token) {
    SignedJWT signedJWT = getOrThrow(() -> SignedJWT.parse(token),
        new JwtAuthenticationException(JwtAuthenticationErrorCode.TOKEN_CANT_BE_PARSED));

    boolean verified = getOrThrow(() -> signedJWT.verify(verifier),
        new JwtAuthenticationException(JwtAuthenticationErrorCode.INVALID_TOKEN));

    if (!verified) {
      throw new JwtAuthenticationException(JwtAuthenticationErrorCode.INVALID_TOKEN);
    }

    Date expirationTime = getOrThrow(() -> signedJWT.getJWTClaimsSet().getExpirationTime(),
        new JwtAuthenticationException(JwtAuthenticationErrorCode.PAYLOAD_CANT_BE_PARSED));

    if (expirationTime == null || new Date().after(expirationTime)) {
      throw new JwtAuthenticationException(JwtAuthenticationErrorCode.EXPIRED_TOKEN);
    }
  }

  private String buildToken(String subject, String authorities, long expirationMillis) {
    long now = System.currentTimeMillis();

    JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
        .subject(subject)
        .issueTime(new Date(now))
        .expirationTime(new Date(now + expirationMillis));

    if (StringUtils.hasText(authorities)) {
      builder.claim(AUTHORITIES_KEY, authorities);
    }

    SignedJWT signedJWT = new SignedJWT(header, builder.build());
    runOrThrow(() -> signedJWT.sign(signer),
        new JwtAuthenticationException(JwtAuthenticationErrorCode.INVALID_TOKEN));

    return signedJWT.serialize();
  }

  private <T> T getOrThrow(JwtCheckedExceptionSupplier<T> supplier,
      JwtAuthenticationException exception) {
    try {
      return supplier.get();
    } catch (Exception e) {
      log.error(exception.getMessage(), e);
      throw exception;
    }
  }

  private void runOrThrow(JwtCheckedExceptionRunnable runnable,
      JwtAuthenticationException exception) {
    try {
      runnable.run();
    } catch (Exception e) {
      log.error(exception.getMessage(), e);
      throw exception;
    }
  }

  @FunctionalInterface
  private interface JwtCheckedExceptionSupplier<T> {

    T get() throws Exception;
  }

  @FunctionalInterface
  private interface JwtCheckedExceptionRunnable {

    void run() throws Exception;
  }

  private long getAccessTokenExpiration() {
    return jwtProperties.accessToken().expiration() * 1000;
  }

  private long getRefreshTokenExpiration() {
    return jwtProperties.refreshToken().expiration() * 1000;
  }
}
