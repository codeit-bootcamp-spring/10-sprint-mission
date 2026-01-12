package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void addUser(UUID UserId, UUID ChannelId);
    void removeUser(UUID channelId, UUID userId);
    void create(Channel channel);
    Channel findById(UUID id);
    List<Channel> findAll();
    void update(UUID id, String name, String description);
    void delete(UUID id);
}
