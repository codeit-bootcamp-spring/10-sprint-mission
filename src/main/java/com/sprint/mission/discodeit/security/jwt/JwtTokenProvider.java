package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.service.auth.DiscodeitUserDetails;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

  public Authentication getAuthentication(String token) {

    Map<String, Object> claims = getClaims(token);

    String email = String.valueOf(claims.get("sub"));

    List<String> roles =
        claims.get("roles") == null
            ? List.of()
            : ((List<?>) claims.get("roles"))
                .stream()
                .map(String::valueOf)
                .toList();

    Object userIdClaim = claims.get("userId");

    UUID userId = userIdClaim == null
        ? null
        : UUID.fromString(String.valueOf(userIdClaim));

    List<GrantedAuthority> authorities = roles.stream()
        .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
        .toList();

    Role role = roles.isEmpty()
        ? null
        : Role.valueOf(roles.get(0).replace("ROLE_", ""));

    String username = String.valueOf(claims.get("username"));

    UserDto userDto = UserDto.builder()
        .id(userId)
        .email(email)
        .username(username)
        .role(role)
        .build();

    DiscodeitUserDetails userDetails =
        new DiscodeitUserDetails(userDto, null);

    return new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        authorities
    );
  }
}
