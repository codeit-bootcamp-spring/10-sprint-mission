package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message createMessage(Channel channel, User user, String message);
    Optional<Message> findMessage(UUID uuid);
    List<Message> findMessagesByChannel(Channel channel);
    List<Message> findAllMessages();
    Message updateMessage(UUID uuid, String newMessage);
    void deleteMessage(UUID uuid);
}
