package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface MessageService {
    // Create
    Message create(UUID userId, UUID channelId, String content);

    // Read
    Message findById(UUID id);

    // ReadAll
    List<Message> readAll();

    // Update
    Message update(UUID messageId, String newContent);

    List<Message> searchMessage(UUID channelId, String msg);

    UUID sendDirectMessage(UUID senderId, UUID receiverId, String content);

    // Delete
    void delete(UUID id);

}
