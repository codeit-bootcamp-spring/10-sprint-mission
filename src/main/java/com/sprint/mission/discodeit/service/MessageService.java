package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // Create
    Message create(Message message);

    // Read
    Message read(UUID id);

    // ReadAll
    List<Message> readAll();

    // Update
    Message update(Message message);

    // Delete
    void delete(UUID id);

}
