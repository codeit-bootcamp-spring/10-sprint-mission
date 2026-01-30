package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDTO.Response createPrivate(UUID creatorId, ChannelDTO.CreatePrivate createRequest);
    ChannelDTO.Response createPublic(ChannelDTO.CreatePublic createRequest);
    ChannelDTO.Response findById(UUID channelId);
    List<ChannelDTO.Response> findAllByUserId(UUID userId);
    void joinChannel(UUID userId, UUID channelId);
    void leaveChannel(UUID userId, UUID channelId);
    ChannelDTO.Response update(ChannelDTO.Update updateRequest);
    void delete(UUID channelId);
}
