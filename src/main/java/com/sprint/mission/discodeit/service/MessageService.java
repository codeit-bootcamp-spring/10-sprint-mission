package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(String content, UUID channelId, UUID userId);
    List<Message> findAllByChannel(UUID channelId);
    Message findById(UUID messageId);
    List<Message> findAll();
    Message update(UUID messageId, String content);
    void delete(UUID messageId);
}
