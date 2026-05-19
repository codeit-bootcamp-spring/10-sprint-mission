package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;

public interface ChannelService {

	ChannelDto create(PublicChannelCreateRequest request);

	ChannelDto create(PrivateChannelCreateRequest request);

	ChannelDto find(UUID channelId);

	List<ChannelDto> findAllByUserId(UUID userId);

	ChannelDto update(UUID channelId, PublicChannelUpdateRequest request);

	void delete(UUID channelId);
}