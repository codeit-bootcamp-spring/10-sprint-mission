package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDto.Response createPrivate(UUID creatorId, ChannelDto.CreatePrivate createRequest);
    ChannelDto.Response createPublic(UUID creatorId, ChannelDto.CreatePublic createRequest);
    ChannelDto.Response findById(UUID channelId);
    List<ChannelDto.Response> findAllByUserId(UUID userId);
    void joinChannel(UUID userId, UUID channelId);
    void leaveChannel(UUID userId, UUID channelId);
    ChannelDto.Response update(ChannelDto.Update updateRequest);
    void delete(UUID channelId);
}
