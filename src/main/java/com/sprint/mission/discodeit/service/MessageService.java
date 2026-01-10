package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface MessageService {
    Message createMessage(Channel channel, User author, String content );

    Message getMessage(UUID id);

    List<Message> getAllMessages();

    void updateMessage(String content, UUID id);

    void deleteMessage(UUID id);
}
