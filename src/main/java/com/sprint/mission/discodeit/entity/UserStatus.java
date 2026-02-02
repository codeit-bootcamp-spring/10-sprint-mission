package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import lombok.Getter;

@Getter
public class UserStatus extends Base {
	private UUID userId;
	private Instant lastAccessedTime;

	public UserStatus(UUID userId) {
		this.userId = userId;
		this.lastAccessedTime = Instant.now();
	}

	public void updateLastAccessedTime() {
		this.lastAccessedTime = Instant.now();
	}

	public boolean isLogined() {
		return Instant.now().isBefore(lastAccessedTime.plus(5, ChronoUnit.MINUTES));
	}

}
