package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void create(Message message);

    Message findById(UUID id);
    List<Message> findAllByChannelId(UUID channelId);

    void update(UUID id, String content);
    void delete(UUID id);
}
