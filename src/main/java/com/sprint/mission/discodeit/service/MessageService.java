package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.nio.channels.Channel;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(String content, User sender, Channel channel);
    Message getMessage(UUID messageId);
    List<Message> getAllMessages();
    Message updateMessage(Message message);
    Message deleteMessage(Message message);
}
