package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublic(ChannelDto.CreatePublicRequest request);
    Channel createPrivate(ChannelDto.CreatePrivateRequest request);
    ChannelDto.Response find(UUID channelId);
    List<ChannelDto.Response> findAllByUserId(UUID userId);
    Channel update(UUID channelId, ChannelDto.UpdatePublicRequest request);
    void delete(UUID channelId);
}
