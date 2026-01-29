package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;

@Getter
public class ReadStatus extends Base {
	private UUID userId;
	private UUID channelId;
	private Instant lastReadTime;

	public ReadStatus(UUID userId, UUID channelId, Instant lastReadTime) {
		this.userId = userId;
		this.channelId = channelId;
		this.lastReadTime = lastReadTime;
	}

	public void updateLastReadTime() {
		this.lastReadTime = Instant.now();
	}
}