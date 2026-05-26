package com.sprint.mission.discodeit.security.jwt;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

	private static final String TOKEN_TYPE_CLAIM = "token_type";
	private static final String USERNAME_CLAIM = "username";
	private static final String ROLE_CLAIM = "role";
	private static final int MIN_HMAC_SECRET_LENGTH = 32;

	private final JwtProperties jwtProperties;

	public String generateAccessToken(DiscodeitUserDetails userDetails) {
		return generateAccessToken(
			userDetails.getUserDto().id(),
			userDetails.getUsername(),
			userDetails.getUserDto().role()
		);
	}

	public String generateAccessToken(UUID userId, String username, Role role) {
		return generateToken(userId, username, role, JwtTokenType.ACCESS, jwtProperties.getAccessTokenValiditySeconds());
	}

	public String generateRefreshToken(DiscodeitUserDetails userDetails) {
		return generateRefreshToken(
			userDetails.getUserDto().id(),
			userDetails.getUsername(),
			userDetails.getUserDto().role()
		);
	}

	public String generateRefreshToken(UUID userId, String username, Role role) {
		return generateToken(userId, username, role, JwtTokenType.REFRESH, jwtProperties.getRefreshTokenValiditySeconds());
	}

	public String refreshAccessToken(String refreshToken) {
		JWTClaimsSet claims = getClaims(refreshToken);
		validateTokenType(claims, JwtTokenType.REFRESH);

		return generateAccessToken(
			UUID.fromString(claims.getSubject()),
			getStringClaim(claims, USERNAME_CLAIM),
			Role.valueOf(getStringClaim(claims, ROLE_CLAIM))
		);
	}

	public boolean validateToken(String token) {
		try {
			getClaims(token);
			return true;
		} catch (JwtTokenException exception) {
			return false;
		}
	}

	public boolean validateAccessToken(String token) {
		return validateToken(token, JwtTokenType.ACCESS);
	}

	public boolean validateRefreshToken(String token) {
		return validateToken(token, JwtTokenType.REFRESH);
	}

	public JWTClaimsSet getClaims(String token) {
		try {
			if (!StringUtils.hasText(token)) {
				throw new JwtTokenException("JWT 토큰이 비어 있습니다.");
			}

			SignedJWT signedJwt = SignedJWT.parse(token);
			if (!signedJwt.verify(new MACVerifier(getSecretBytes()))) {
				throw new JwtTokenException("JWT 서명이 올바르지 않습니다.");
			}

			JWTClaimsSet claims = signedJwt.getJWTClaimsSet();
			validateIssuer(claims);
			validateExpiration(claims);
			return claims;
		} catch (ParseException exception) {
			throw new JwtTokenException("JWT 토큰 형식이 올바르지 않습니다.", exception);
		} catch (JOSEException exception) {
			throw new JwtTokenException("JWT 토큰 검증에 실패했습니다.", exception);
		}
	}

	public UUID getUserId(String token) {
		return UUID.fromString(getClaims(token).getSubject());
	}

	public String getUsername(String token) {
		return getStringClaim(getClaims(token), USERNAME_CLAIM);
	}

	public Role getRole(String token) {
		return Role.valueOf(getStringClaim(getClaims(token), ROLE_CLAIM));
	}

	public JwtTokenType getTokenType(String token) {
		return parseTokenType(getStringClaim(getClaims(token), TOKEN_TYPE_CLAIM));
	}

	private String generateToken(UUID userId, String username, Role role, JwtTokenType tokenType, long validitySeconds) {
		try {
			Instant issuedAt = Instant.now();
			Instant expiresAt = issuedAt.plusSeconds(validitySeconds);

			JWTClaimsSet claims = new JWTClaimsSet.Builder()
				.issuer(jwtProperties.getIssuer())
				.subject(userId.toString())
				.issueTime(Date.from(issuedAt))
				.expirationTime(Date.from(expiresAt))
				.jwtID(UUID.randomUUID().toString())
				.claim(TOKEN_TYPE_CLAIM, tokenType.name())
				.claim(USERNAME_CLAIM, username)
				.claim(ROLE_CLAIM, role.name())
				.build();

			SignedJWT signedJwt = new SignedJWT(
				new JWSHeader.Builder(JWSAlgorithm.HS256).type(com.nimbusds.jose.JOSEObjectType.JWT).build(),
				claims
			);
			signedJwt.sign(new MACSigner(getSecretBytes()));
			return signedJwt.serialize();
		} catch (JOSEException exception) {
			throw new JwtTokenException("JWT 토큰 발급에 실패했습니다.", exception);
		}
	}

	private boolean validateToken(String token, JwtTokenType expectedType) {
		try {
			validateTokenType(getClaims(token), expectedType);
			return true;
		} catch (JwtTokenException exception) {
			return false;
		}
	}

	private void validateTokenType(JWTClaimsSet claims, JwtTokenType expectedType) {
		JwtTokenType actualType = parseTokenType(getStringClaim(claims, TOKEN_TYPE_CLAIM));
		if (actualType != expectedType) {
			throw new JwtTokenException("JWT 토큰 타입이 올바르지 않습니다.");
		}
	}

	private void validateIssuer(JWTClaimsSet claims) {
		if (!Objects.equals(jwtProperties.getIssuer(), claims.getIssuer())) {
			throw new JwtTokenException("JWT 발급자가 올바르지 않습니다.");
		}
	}

	private void validateExpiration(JWTClaimsSet claims) {
		Date expirationTime = claims.getExpirationTime();
		if (expirationTime == null || !expirationTime.after(new Date())) {
			throw new JwtTokenException("JWT 토큰이 만료되었습니다.");
		}
	}

	private JwtTokenType parseTokenType(String tokenType) {
		try {
			return JwtTokenType.valueOf(tokenType);
		} catch (IllegalArgumentException exception) {
			throw new JwtTokenException("JWT 토큰 타입이 올바르지 않습니다.", exception);
		}
	}

	private String getStringClaim(JWTClaimsSet claims, String claimName) {
		try {
			String claimValue = claims.getStringClaim(claimName);
			if (!StringUtils.hasText(claimValue)) {
				throw new JwtTokenException("JWT 클레임이 비어 있습니다: " + claimName);
			}
			return claimValue;
		} catch (ParseException exception) {
			throw new JwtTokenException("JWT 클레임을 읽을 수 없습니다: " + claimName, exception);
		}
	}

	private byte[] getSecretBytes() {
		String secret = jwtProperties.getSecret();
		if (!StringUtils.hasText(secret)) {
			throw new IllegalStateException("JWT_SECRET 또는 discodeit.security.jwt.secret 설정이 필요합니다.");
		}

		byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
		if (secretBytes.length < MIN_HMAC_SECRET_LENGTH) {
			throw new IllegalStateException("JWT secret은 HS256 사용을 위해 32바이트 이상이어야 합니다.");
		}
		return secretBytes;
	}
}
