package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    void createChannel(Channel channel);

    Channel findId(UUID id);

    List<Channel> findAll();

    void update(Channel channel, String channelName);

    void delete(UUID id);

}
