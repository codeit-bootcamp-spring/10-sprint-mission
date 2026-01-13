package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // Create
    Message create(UUID userId, UUID channelId, String content);

    // Read
    Message findById(UUID id);

    // ReadAll
    List<Message> readAll();

    // Update
    Message update(UUID id);

    // Delete
    void delete(UUID id);

}
