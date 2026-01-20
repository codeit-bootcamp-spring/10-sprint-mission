package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageType;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    public Message createMessage(String message, UUID userId, UUID channelId, MessageType type);

    public Message searchMessage(UUID targetMessageId);

    public List<Message> searchMessageAll();

    public Message updateMessage(UUID targetMessageId, String newMessage);

    public void deleteMessage(UUID targetMessageId);
}
