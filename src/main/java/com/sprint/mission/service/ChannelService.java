package com.sprint.mission.service;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(UUID userID, String name);

//    Channel findById(UUID id);

//    List<User> findByChannelId(UUID channelId);

//    List<Channel> findAll();

    Channel update(UUID channelId, String name);

//    void deleteById(UUID id);

    Channel joinChannel(UUID userId, UUID channelId);

    Channel leaveChannel(UUID userId, UUID channelId);

    Channel getChannelOrThrow(UUID channelId);

    List<Channel> getAllChannel();

    void deleteChannel(UUID channelId);

    List<Channel> getUserChannels(UUID userId);
}
