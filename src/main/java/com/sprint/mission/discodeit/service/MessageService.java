package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(UUID userId, UUID channelId, String content);

    Message findById(UUID id);
    List<Message> findAll();
    List<Message> findMessagesByChannelId(UUID channelId);
    List<Message> findMessagesByUserId(UUID userId);

    Message update(UUID id, String updateContent);

    void deleteMessageByMessageId(UUID id);
    void deleteMessagesByUserId(UUID id);
    void deleteMessagesByChannelId(UUID channelId);

    void save(Message message);
}
