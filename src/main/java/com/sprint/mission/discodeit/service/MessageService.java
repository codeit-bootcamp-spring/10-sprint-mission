package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.*;

public interface MessageService {
    void create();

    Optional<Message> read(UUID id);

    Optional<ArrayList<Message>> readAll();

    void update(Message messageData);

    Message delete(UUID id);
}
