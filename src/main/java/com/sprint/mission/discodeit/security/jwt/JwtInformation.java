package com.sprint.mission.discodeit.security.jwt;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.springframework.util.StringUtils;

public record JwtInformation(
	UUID userId,
	String accessToken,
	String refreshToken,
	Instant accessTokenExpiresAt,
	Instant refreshTokenExpiresAt
) {

	public JwtInformation {
		Objects.requireNonNull(userId, "userId는 필수입니다.");
		Objects.requireNonNull(accessTokenExpiresAt, "accessTokenExpiresAt은 필수입니다.");
		Objects.requireNonNull(refreshTokenExpiresAt, "refreshTokenExpiresAt은 필수입니다.");
		validateToken(accessToken, "accessToken");
		validateToken(refreshToken, "refreshToken");
	}

	public JwtInformation rotate(
		String accessToken,
		String refreshToken,
		Instant accessTokenExpiresAt,
		Instant refreshTokenExpiresAt
	) {
		return new JwtInformation(
			this.userId,
			accessToken,
			refreshToken,
			accessTokenExpiresAt,
			refreshTokenExpiresAt
		);
	}

	public boolean hasAccessToken(String accessToken) {
		return this.accessToken.equals(accessToken);
	}

	public boolean hasRefreshToken(String refreshToken) {
		return this.refreshToken.equals(refreshToken);
	}

	public boolean isAccessTokenExpired() {
		return isAccessTokenExpired(Instant.now());
	}

	public boolean isAccessTokenExpired(Instant now) {
		return !accessTokenExpiresAt.isAfter(now);
	}

	public boolean isRefreshTokenExpired() {
		return isRefreshTokenExpired(Instant.now());
	}

	public boolean isRefreshTokenExpired(Instant now) {
		return !refreshTokenExpiresAt.isAfter(now);
	}

	public boolean isExpired() {
		return isRefreshTokenExpired();
	}

	public boolean isExpired(Instant now) {
		return isRefreshTokenExpired(now);
	}

	private static void validateToken(String token, String fieldName) {
		if (!StringUtils.hasText(token)) {
			throw new IllegalArgumentException(fieldName + "은 필수입니다.");
		}
	}
}
