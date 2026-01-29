package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(String name, String description);
    void delete(UUID channelId);
    Channel findById(UUID id);
    List<Channel> findAll();
    Channel update(UUID id, String name, String description);
}
