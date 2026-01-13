package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel create(String channelName, String type, User user);
    Channel findById(Channel channel);
    List<Channel> findAll();
    Channel update(Channel channel, String name);
    void delete(Channel channel);
}
