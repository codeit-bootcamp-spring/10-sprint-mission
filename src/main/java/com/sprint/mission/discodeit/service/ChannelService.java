package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.*;

public interface ChannelService {
    Channel createChannel(String name, String type);

    Channel getChannel(UUID id);

    List<Channel> getAllChannels();

    void updateChannel(UUID id, String name, String type);

    void deleteChannel(UUID id);
}
