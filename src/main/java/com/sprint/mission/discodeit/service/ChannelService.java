package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String name, String description);
    Channel findById(UUID id);
    List<Channel> findAll();
    void updateChannel(UUID id, String name, String description);
    void delete(UUID id);
}
