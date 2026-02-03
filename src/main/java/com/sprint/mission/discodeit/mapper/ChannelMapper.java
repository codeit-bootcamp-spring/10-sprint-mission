package com.sprint.mission.discodeit.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.dto.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.PrivateChannelPostDTO;
import com.sprint.mission.discodeit.dto.PublicChannelPostDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

@Component
public class ChannelMapper {
	public Channel toChannel(PublicChannelPostDTO publicChannelPostDTO) {
		return new Channel(
			ChannelType.PUBLIC,
			publicChannelPostDTO.name(),
			publicChannelPostDTO.description()
		);
	}

	public Channel toChannel(PrivateChannelPostDTO privateChannelPostDTO) {
		return new Channel(
			ChannelType.PUBLIC,
			"",
			""
		);
	}

	// public Channel toChannel(Private)

	public ChannelResponseDTO fromChannel(Channel channel, Instant lastMessageTime) {
		return new ChannelResponseDTO(
			channel.getChannelType(),
			channel.getName(),
			channel.getDescription(),
			lastMessageTime,
			channel.getChannelType() == ChannelType.PRIVATE ? channel.getUserIds() : null
		);
	}

}
