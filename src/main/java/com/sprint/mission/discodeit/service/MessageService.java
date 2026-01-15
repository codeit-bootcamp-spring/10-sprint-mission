package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message send(String text, UUID userId, UUID channelId);
    List<Message> findMessagesByUserAndChannel(UUID userId, UUID channelId);
    List<Message> findMessagesByChannel(UUID channelId);
    List<Message> findMessagesByUser(UUID userId);
    List<Message> findAllMessages();
    Message getMessage(UUID messageId);
    Message updateMessage(UUID messageId, String text);
    void deleteMessage(UUID messageId);
}
