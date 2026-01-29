package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
<<<<<<< HEAD
    Message create(String content, UUID channelId, UUID authorId);
    Message find(UUID messageId);
    List<Message> findAll();
    Message update(UUID messageId, String newContent);
    void delete(UUID messageId);
=======

    Message createMessage(UUID userId, UUID channelId, String content);
    Message findById(UUID id);
    List<Message> findAll();
    void updateMessage(UUID id, String content, UUID userId, UUID channelId);
    void delete(UUID id);
>>>>>>> upstream/김혜성
}
