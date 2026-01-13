package com.sprint.mission.service;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(UUID userID, String name);

    Channel findById(UUID id);

    List<Channel> findAll();

    Channel update(UUID id, String name);

    void deleteById(UUID id);

    Channel joinChannel(UUID userID, UUID channelId);

    Channel leaveChannel(UUID userID, UUID channelId);

    List<User> findByChannelId(UUID channelId);
}
