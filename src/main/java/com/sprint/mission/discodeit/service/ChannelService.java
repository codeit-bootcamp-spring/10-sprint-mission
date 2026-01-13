package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.*;

public interface ChannelService {
    Channel createChannel(String name, String type);

    Channel getChannel(UUID id);

    List<Channel> getAllChannels();

    Channel updateChannel(UUID id, String name, String type);

    void deleteChannel(UUID id);

    void enterChannel(UUID userId, UUID channelId);

    void leaveChannel(UUID userId, UUID channelId);

    List<Channel> getChannelsByUserId(UUID userId);

    void validateChannel(Channel channel);
}
