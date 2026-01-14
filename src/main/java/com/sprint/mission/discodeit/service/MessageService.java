package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void createMessage(Message message);

    void changeMessage(Message oldMessage, Message newMessage);

    void deleteMessage(Message message);

    List<Message> getMessages(List<UUID> uuids);

    void deleteMessages(List<UUID> messageUUIDs);
}
