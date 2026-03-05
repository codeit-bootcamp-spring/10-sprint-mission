package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.*;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDto createPublicChannel(CreatePublicChannelRequestDTO dto);

    ChannelDto createPrivateChannel(CreatePrivateChannelRequestDTO dto);

    List<ChannelDto> findAllByUserId(UUID userId);

    ChannelDto findByChannelId(UUID channelId);

    ChannelDto updateChannel(UUID channelId, UpdateChannelRequestDTO dto);

    void deleteChannel(UUID channelId);
}
