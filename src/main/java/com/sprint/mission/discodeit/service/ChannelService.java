package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.ChannelDTO.ChannelCreateDTO;
import com.sprint.mission.discodeit.DTO.ChannelDTO.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.DTO.ChannelDTO.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublicChannel(PublicChannelCreateDTO req);
    Channel createPrivateChannel(PrivateChannelCreateDTO req);
    Channel find(UUID channelId);
    List<Channel> findAll();
    Channel update(UUID channelId, String newName, String newDescription);
    void delete(UUID channelId);
}
