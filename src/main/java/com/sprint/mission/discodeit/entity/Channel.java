package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.Getter;

@Getter
public class Channel extends Base {
	private final List<UUID> userIds;
	private final List<UUID> messageIds;
	private ChannelType channelType;
	private String name;
	private String description;

	public Channel(ChannelType channelType, String name, String description) {
		this.channelType = channelType;
		this.name = name;
		this.description = description;
		this.userIds = new ArrayList<>();
		this.messageIds = new ArrayList<>();
	}

	public void updateName(String name) {
		this.name = name;
		updateUpdatedAt(Instant.now());
	}

	public void updateDescription(String description) {
		this.description = description;
		updateUpdatedAt(Instant.now());
	}
	
	public void addUserId(UUID userId) {
		this.userIds.add(userId);
	}

	public void addMessage(UUID messageId) {
		this.messageIds.add(messageId);
	}

	@Override
	public String toString() {
		return "{" + name + userIds + "}";

	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Channel channel))
			return false;
		return Objects.equals(this.getId(), channel.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.getId());
	}
}
