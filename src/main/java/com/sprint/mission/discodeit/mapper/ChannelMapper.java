package com.sprint.mission.discodeit.mapper;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.PrivateChannelPostDto;
import com.sprint.mission.discodeit.dto.PublicChannelPostDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

@Component
public class ChannelMapper {
	public Channel toChannel(PublicChannelPostDto publicChannelPostDto) {
		return new Channel(
			ChannelType.PUBLIC,
			publicChannelPostDto.name(),
			publicChannelPostDto.description()
		);
	}

	public Channel toChannel(PrivateChannelPostDto privateChannelPostDto) {
		Channel channel = new Channel(
			ChannelType.PRIVATE,
			"",
			""
		);

		for (UUID userId : privateChannelPostDto.userIds()) {
			channel.addUserId(userId);
		}

		return channel;
	}

	// public Channel toChannel(Private)

	public ChannelResponseDto fromChannel(Channel channel, Instant lastMessageTime) {
		return new ChannelResponseDto(
			channel.getId(),
			channel.getChannelType(),
			channel.getName(),
			channel.getDescription(),
			lastMessageTime,
			channel.getChannelType() == ChannelType.PRIVATE ? channel.getUserIds() : null
		);
	}

}
