package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(User user, Channel channel, String contents);
    Message findById(UUID id);
    List<Message> findAll();
    Message update(UUID id, String content, User user, Channel channel);

    void delete(UUID id);
}
