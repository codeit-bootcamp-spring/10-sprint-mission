package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(ChannelType type, String name, String description);
    Channel findChannelById(UUID channelId);
    Channel findChannelByName(String name);
    List<Channel> findAllChannel();
    List<Channel> findChannelsByUser(UUID userId);
    Channel update(UUID channelId, String name, String description);
    void delete(UUID channelId);
    void joinChannel(UUID channelId, UUID userId);
    void leaveChannel(UUID channelId, UUID userId);
    boolean isUserInChannel(UUID channelId, UUID userId);
}
