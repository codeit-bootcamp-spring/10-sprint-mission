package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {


    Message create(String content, UUID userId, UUID channelId);

    Message findById(UUID id);

    List<Message> findAll();

    Message update(UUID id, String content);

    Message delete(UUID id);

    List<Message> findByUser(UUID userId);
    void removeUser(UUID userId);
    void removeChannel(UUID channelId);


}
