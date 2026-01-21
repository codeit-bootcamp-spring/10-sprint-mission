package com.sprint.mission.descodeit.repository;

import com.sprint.mission.descodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message findById(UUID messageId);
    List<Message> findAll();
    void save(UUID messageId , Message message);
    void delete(UUID messageId);
}
