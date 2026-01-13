package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void create(String message, User user, Channel channel);

    Message findById(UUID id);
    List<Message> findAllByChannelId(UUID channelId);
    void update(UUID id, String content);
    void delete(UUID id);
}
