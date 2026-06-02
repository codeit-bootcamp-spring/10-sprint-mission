package com.sprint.mission.discodeit.security.jwt;

import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class InMemoryJwtRegistry implements JwtRegistry {

	private static final int DEFAULT_MAX_ACTIVE_JWT_COUNT = 1;

	private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
	private final int maxActiveJwtCount = DEFAULT_MAX_ACTIVE_JWT_COUNT;

	@Override
	public void registerJwtInformation(JwtInformation jwtInformation) {
		if (jwtInformation == null) {
			return;
		}

		origin.compute(jwtInformation.userId(), (userId, jwtInformationQueue) -> {
			Queue<JwtInformation> activeJwtInformationQueue = jwtInformationQueue == null
				? new ConcurrentLinkedQueue<>()
				: jwtInformationQueue;

			clearExpiredJwtInformation(activeJwtInformationQueue, Instant.now());
			activeJwtInformationQueue.add(jwtInformation);
			trimJwtInformation(activeJwtInformationQueue);
			return activeJwtInformationQueue;
		});
	}

	@Override
	public void invalidateJwtInformationByUserId(UUID userId) {
		if (userId == null) {
			return;
		}
		origin.remove(userId);
	}

	@Override
	public void invalidateJwtInformationByRefreshToken(String refreshToken) {
		if (!StringUtils.hasText(refreshToken)) {
			return;
		}

		origin.forEach((userId, jwtInformationQueue) -> jwtInformationQueue.removeIf(
			jwtInformation -> jwtInformation.hasRefreshToken(refreshToken)
		));
		removeEmptyJwtInformationQueues();
	}

	@Override
	public boolean hasActiveJwtInformationByUserId(UUID userId) {
		if (userId == null) {
			return false;
		}

		clearExpiredJwtInformation();
		Queue<JwtInformation> jwtInformationQueue = origin.get(userId);
		return jwtInformationQueue != null && jwtInformationQueue.stream()
			.anyMatch(jwtInformation -> !jwtInformation.isRefreshTokenExpired());
	}

	@Override
	public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
		if (!StringUtils.hasText(accessToken)) {
			return false;
		}

		clearExpiredJwtInformation();
		return origin.values().stream()
			.flatMap(Queue::stream)
			.anyMatch(jwtInformation -> jwtInformation.hasAccessToken(accessToken)
				&& !jwtInformation.isAccessTokenExpired());
	}

	@Override
	public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
		if (!StringUtils.hasText(refreshToken)) {
			return false;
		}

		clearExpiredJwtInformation();
		return origin.values().stream()
			.flatMap(Queue::stream)
			.anyMatch(jwtInformation -> jwtInformation.hasRefreshToken(refreshToken)
				&& !jwtInformation.isRefreshTokenExpired());
	}

	@Override
	public boolean rotateJwtInformation(String refreshToken, JwtInformation jwtInformation) {
		if (!StringUtils.hasText(refreshToken) || jwtInformation == null) {
			return false;
		}

		clearExpiredJwtInformation();
		AtomicBoolean rotated = new AtomicBoolean(false);
		origin.computeIfPresent(jwtInformation.userId(), (userId, jwtInformationQueue) -> {
			boolean removed = jwtInformationQueue.removeIf(storedJwtInformation ->
				storedJwtInformation.hasRefreshToken(refreshToken) && !storedJwtInformation.isRefreshTokenExpired()
			);
			if (removed) {
				jwtInformationQueue.add(jwtInformation);
				trimJwtInformation(jwtInformationQueue);
				rotated.set(true);
			}
			return jwtInformationQueue.isEmpty() ? null : jwtInformationQueue;
		});
		return rotated.get();
	}

	@Scheduled(fixedDelay = 1000 * 60 * 5)
	@Override
	public void clearExpiredJwtInformation() {
		Instant now = Instant.now();
		origin.forEach((userId, jwtInformationQueue) -> clearExpiredJwtInformation(jwtInformationQueue, now));
		removeEmptyJwtInformationQueues();
	}

	private void clearExpiredJwtInformation(Queue<JwtInformation> jwtInformationQueue, Instant now) {
		jwtInformationQueue.removeIf(jwtInformation -> jwtInformation.isExpired(now));
	}

	private void trimJwtInformation(Queue<JwtInformation> jwtInformationQueue) {
		while (jwtInformationQueue.size() > maxActiveJwtCount) {
			jwtInformationQueue.poll();
		}
	}

	private void removeEmptyJwtInformationQueues() {
		origin.entrySet().removeIf(entry -> entry.getValue().isEmpty());
	}
}
