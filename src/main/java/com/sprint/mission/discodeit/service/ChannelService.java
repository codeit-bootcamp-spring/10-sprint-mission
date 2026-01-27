package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(String name);
    Channel joinUsers(UUID channelId,UUID ...userId);
    Channel findChannel(UUID channelId);
    List<Channel> findAllChannel();
    List<Channel> findAllChannelsByUserId(UUID userId);
    Channel update(UUID channelId, String newName);
    void delete(UUID channelId);
    void save(Channel channel);
}
