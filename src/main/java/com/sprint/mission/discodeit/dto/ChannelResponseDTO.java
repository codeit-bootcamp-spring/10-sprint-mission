package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.ChannelType;

public record ChannelResponseDTO(
	ChannelType channelType,
	String name,
	String description,
	Instant lastMessagedTime,
	List<UUID> userIds // private 채널인 경우
) {
}

