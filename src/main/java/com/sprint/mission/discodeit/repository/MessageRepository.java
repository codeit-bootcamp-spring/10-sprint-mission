package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message createMessage(UUID userId, UUID channelId, String content);
    Message findMessage(UUID messageId);
    List<Message> findAllMessage();
    List<Message> findAllByChannelMessage(UUID channelId);
    void deleteMessage(UUID messageId);
    Message updateMessage(UUID messageId, String content);
}