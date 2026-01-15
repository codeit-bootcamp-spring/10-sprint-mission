package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface MessageService {
    Message create(String msg, User user, Channel channel);

    Message read(UUID id);

    List<Message> readAll();

    Message update(UUID id, String messageData);

    void delete(UUID id);
}
