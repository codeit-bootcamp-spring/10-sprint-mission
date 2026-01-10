package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(UUID channelId, UUID userId, String msg);
    List<Message> read(UUID channelId);
    Message update(UUID channelId, UUID userId, UUID messageId, String newMessage);
    void delete(UUID channelId, UUID userId, UUID messageId);
}
