package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(String name, User user);
    Channel read(UUID channelId);
    List<Channel> readAll();
    Channel update(UUID channelId, String name);
    void delete(UUID channelId);
    List<User> readUsers(UUID channelId);
    void join(UUID channelId, UUID userId);
}
