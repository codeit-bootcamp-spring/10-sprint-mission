package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message createMessage(UUID userId, UUID channelId, String content);
    Message findById(UUID id);
    List<Message> findAll();
    void updateMessage(UUID id, String content, UUID userId, UUID channelId);
    void delete(UUID id);
}
