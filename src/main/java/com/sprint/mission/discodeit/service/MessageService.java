package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void createMessage(Message message);

    Message findById(UUID id);

    List<Message> findAll();

    void updateById(UUID id, String content);

    void deleteById(UUID id);

    void printAllMessages();

}
