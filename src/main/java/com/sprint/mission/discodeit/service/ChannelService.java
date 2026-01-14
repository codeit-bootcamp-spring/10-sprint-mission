package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel create(String channelName, String type, User user);
    Channel findById(UUID id);
    List<Channel> findAll();
    Channel update(UUID id, String name);
    Channel delete(UUID id);
    List<User> enter(UUID channelId,UUID userId);
    List<User> leave(UUID channelId,UUID userId);
}
