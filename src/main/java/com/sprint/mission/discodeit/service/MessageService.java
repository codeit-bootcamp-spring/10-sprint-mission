package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void createMessage(Message message);
    Message findId(UUID id);
    List<Message> findAll();
    void update(Message msg, String content);
    List<Message> findByChannelId(UUID channelId);
    void delete(UUID id);
    void deleteByChannelId(UUID channelId);
}
