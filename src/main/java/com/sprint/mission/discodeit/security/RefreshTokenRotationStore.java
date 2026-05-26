package com.sprint.mission.discodeit.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RefreshTokenRotationStore {

	private final Map<String, Instant> usedRefreshTokenIds = new ConcurrentHashMap<>();

	public boolean markUsed(String refreshTokenId, Instant expiresAt) {
		if (!StringUtils.hasText(refreshTokenId) || expiresAt == null) {
			return false;
		}

		clearExpired();
		return usedRefreshTokenIds.putIfAbsent(refreshTokenId, expiresAt) == null;
	}

	public boolean isUsed(String refreshTokenId) {
		clearExpired();
		return StringUtils.hasText(refreshTokenId) && usedRefreshTokenIds.containsKey(refreshTokenId);
	}

	public void clearExpired() {
		Instant now = Instant.now();
		usedRefreshTokenIds.entrySet().removeIf(entry -> !entry.getValue().isAfter(now));
	}
}
