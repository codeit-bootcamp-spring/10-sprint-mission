package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Optional<Message> find(UUID messageID);
    List<Message> findAll();
    void deleteMessage(Message message);
    Message save(Message message);
}
