package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(String content, UUID channelId, UUID userId);
    Message readMessage(UUID id);
    List<Message> readAllMessage();
    void updateMessage(UUID id, String content);
    void deleteMessage(UUID id);
    boolean isMessageDeleted(UUID id);
}
