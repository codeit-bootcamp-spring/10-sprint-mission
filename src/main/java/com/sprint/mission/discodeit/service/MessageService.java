package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.UUID;
import java.util.List;


public interface MessageService {
    void create(Message message);

    Message readById(UUID id);

    List<Message> readAll();

    void update(Message message);

    void delete(UUID id);
}