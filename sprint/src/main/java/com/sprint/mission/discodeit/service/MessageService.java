package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public interface MessageService {
    UUID addMessage(User user, Channel channel, String text);
    Message getMessage(UUID id);
    Iterable<Message> getAllMessages();
    void updateMessage(UUID id, String text);
    void deleteMessage(UUID id);
}
