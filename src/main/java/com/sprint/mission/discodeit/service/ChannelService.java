package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;

public interface ChannelService {

	@PreAuthorize("hasRole('CHANNEL_MANAGER')")
	ChannelDto create(PublicChannelCreateRequest request);

	ChannelDto create(PrivateChannelCreateRequest request);

	ChannelDto find(UUID channelId);

	List<ChannelDto> findAllByUserId(UUID userId);

	@PreAuthorize("hasRole('CHANNEL_MANAGER')")
	ChannelDto update(UUID channelId, PublicChannelUpdateRequest request);

	@PreAuthorize("hasRole('CHANNEL_MANAGER')")
	void delete(UUID channelId);
}