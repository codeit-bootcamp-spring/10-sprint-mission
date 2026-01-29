package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPrivateChannel(ChannelRequestDto channelCreateDto);
    Channel createPublicChannel(ChannelRequestDto channelCreateDto);
    Channel find(UUID channelId);
    List<Channel> findAll();
    Channel update(UUID channelId, String newName, String newDescription);
    void delete(UUID channelId);
}
