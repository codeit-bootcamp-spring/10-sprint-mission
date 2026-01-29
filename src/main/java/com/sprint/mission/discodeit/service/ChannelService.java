package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(String name, String description, ChannelType type);

    Channel findById(UUID id);
    List<Channel> findAll();
    List<Channel> findChannelsByUserId(UUID userId);

    Channel update(UUID id, String name, String description);

    void addChannelByUserId(UUID channelId, UUID userId);
    void removeChannelByUserId(UUID channelId, UUID userId);

    void deleteChannelByChannelId(UUID channelId);

    void save(Channel channel);
}
