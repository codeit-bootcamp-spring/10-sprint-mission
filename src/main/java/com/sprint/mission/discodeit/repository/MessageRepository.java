package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);

    Message findById(UUID id);

    void delete(UUID id);
}
