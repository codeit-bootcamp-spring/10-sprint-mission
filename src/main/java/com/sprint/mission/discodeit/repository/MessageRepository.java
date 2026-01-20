package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);

    void delete(UUID id);

    Message findById(UUID id);

    List<Message> findAll();

    List<Message> findByChannelId(UUID channelId);

    List<Message> findByUserId(UUID userId);

}
