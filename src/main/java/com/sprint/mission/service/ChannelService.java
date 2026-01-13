package com.sprint.mission.service;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(User owner, String name);

    Channel findById(UUID id);

    List<Channel> findAll();

    Channel update(UUID id, String name);

    void deleteById(UUID id);

    Channel joinChannel(User user, UUID channelId);

    Channel leaveChannel(User user, UUID channelId);
}
