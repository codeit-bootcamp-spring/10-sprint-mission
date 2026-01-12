package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(ChannelType type, String channelName, String channelDescription);

    Channel readChannel(UUID id);

    List<Channel> readAllChannel();

    void updateChannel(UUID id, ChannelType type, String channelName, String channelDescription);

    void deleteChannel(UUID id);
}
