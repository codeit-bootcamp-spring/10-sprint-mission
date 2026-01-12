package com.sprint.mission.service;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(String content);

    Message findById(UUID id);

    List<Message> findAll();

    void updateMessage(UUID id, String content);

    void deleteById(UUID id);
}
