package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDto createChannel(ChannelDto.PrivateChannelCreateRequest channelPrivateReq);
    ChannelDto createChannel(ChannelDto.PublicChannelCreateRequest channelPublicReq);
    List<ChannelDto> findAllByUserId(UUID userId);
    ChannelDto updateChannel(UUID uuid, ChannelDto.PublicChannelUpdateRequest channelReq);
    void deleteChannel(UUID uuid);
}
