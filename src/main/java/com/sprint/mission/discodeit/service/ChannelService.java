package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String channelName);

    Channel findId(UUID channelId);

    Channel findName(String name);

    List<Channel> findAll();

    List<Channel> findChannels(UUID userId);

    Channel update(UUID channelId, String channelName);

    void delete(UUID channelId);

    void deleteAll();
}
