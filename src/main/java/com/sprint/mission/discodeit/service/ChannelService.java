package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.IsPrivate;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    // Create
    Channel create(String name, IsPrivate isPrivate, UUID ownerId);

    // Read
    Channel findById(UUID id);

    // ReadAll
    List<Channel> readAll();

    // Update
    Channel update(UUID id);

    // Delete
    void delete(UUID id);


}
