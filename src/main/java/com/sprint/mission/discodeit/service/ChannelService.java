package com.sprint.mission.discodeit.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.ChannelPatchDto;
import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.PrivateChannelPostDto;
import com.sprint.mission.discodeit.dto.PublicChannelPostDto;

public interface ChannelService {
	ChannelResponseDto createPublicChannel(PublicChannelPostDto publicChannelPostDTO);

	ChannelResponseDto createPrivateChannel(PrivateChannelPostDto channelPostDTO);

	ChannelResponseDto findById(UUID channelId);

	List<ChannelResponseDto> findAllByUserId(UUID userId);

	ChannelResponseDto update(UUID channelId, ChannelPatchDto channelPatchDTO);

	ChannelResponseDto addUser(UUID channelId, UUID userId);

	boolean deleteUser(UUID channelId, UUID userId);

	void delete(UUID channelId);

	boolean isUserInvolved(UUID channelId, UUID userId);

	Instant findLastMessageTime(UUID channelId);
}
