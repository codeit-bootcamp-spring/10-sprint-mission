package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.UUID;
import java.util.List;


public interface ChannelService {
    Channel create(String name, String description, String type, boolean isPublic);

    Channel findById(UUID id);

    List<Channel> findAll();

    Channel update(UUID id, String name, String description, boolean isPublic);

    void delete(UUID id);
}