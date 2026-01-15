package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel addChannel(String name, String description, UUID ownerId, ChannelType channelType);
    Channel updateChannelInfo(UUID id, UUID ownerId, String name, String description);
    List<Channel> findAllChannels();
    Channel getChannelById(UUID id);
    void deleteChannelById(UUID id, UUID ownerId);

}
