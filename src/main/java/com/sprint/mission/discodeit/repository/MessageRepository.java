package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {

    void save(Message message);

    void delete(UUID messageId);

    Message updateMessageData(UUID messageId, String messageData);

    List<Message> loadAll();
}
