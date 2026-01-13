package com.sprint.mission.service;

import com.sprint.mission.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(UUID userId, UUID channelId, String content);

    Message findById(UUID id);

    List<Message> findAll();

    Message updateMessage(UUID userId, UUID messageId, String content);

    void deleteById(UUID userId, UUID MessageId);

    List<Message> findByChannelId(UUID channelId);

    List<Message> findByUserIdAndChannelId(UUID userId, UUID channelId);
}
