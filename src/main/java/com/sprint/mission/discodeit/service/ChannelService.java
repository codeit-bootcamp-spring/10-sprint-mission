package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(String channelName);

    Channel findById(UUID id);
    List<Channel> findAll();
    List<Channel> findChannelsByUserId(UUID userId);

    Channel update(UUID id, String updateChannelName);

    void addChannelByUserId(UUID channelId, UUID userId);
    void removeChannelByUserId(UUID channelId, UUID userId);
    void deleteChannelByChannelId(UUID channelId);

    void save(Channel channel);
}
