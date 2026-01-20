package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {

    Channel create(String channelName, String type, UUID ownerId);
    Channel findById(UUID id);
    List<Channel> findAll();
    Channel update(UUID id, String name);
    Channel delete(UUID id);
    void enter(UUID channelId,UUID userId);
    void leave(UUID channelId,UUID userId);

    List<Channel> findByUser(UUID userId);
    List<User> findByChannel(UUID channelId);
    void removeUserFromAllChannel(UUID userId);
}
