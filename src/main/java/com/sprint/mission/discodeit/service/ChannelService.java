package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String channelName);

    Channel findId(UUID channel);

    List<Channel> findAll();

    Channel update(UUID channel, String channelName);

    void delete(UUID channel);
}
