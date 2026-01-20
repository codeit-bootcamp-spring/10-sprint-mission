package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message find(UUID messageID);
    List<Message> findAll();
    void addMessage(Message message);
    void removeMessage(Message message);
    Message save(Message message);
}
