package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    // Create
    Channel create(Channel channel);

    // Read
    Channel read(UUID id);

    // ReadAll
    List<Channel> readAll();

    // Update
    Channel update(Channel channel);

    // Delete
    void delete(UUID id);

    // Clear
    void clear();
}
