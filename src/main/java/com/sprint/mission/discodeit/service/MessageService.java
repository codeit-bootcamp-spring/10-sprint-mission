package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(UUID userId, String text, UUID channelId);
    Message findMessage(UUID messageId);
    List<Message> findMessageByKeyword(UUID channelId, String keyword);
    List<Message> findAllMessages();
    List<Message> findAllMessagesByChannelId(UUID channelId);
    Message update(UUID messageId,UUID requestUserId, String newText);
    void delete(UUID messageId);
    void save(Message message);
}
