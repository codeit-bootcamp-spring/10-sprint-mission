package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDto.Response createPublicChannel(ChannelDto.CreatePublicRequest request);
    ChannelDto.Response createPrivateChannel(ChannelDto.CreatePrivateRequest request);

    ChannelDto.Response findById(UUID id);
    List<ChannelDto.Response> findAllByUserId(UUID userId);

    ChannelDto.Response update(ChannelDto.UpdateRequest request);

    void delete(UUID id);

    void addChannelByUserId(UUID channelId, UUID userId);
    void removeChannelByUserId(UUID channelId, UUID userId);
}
