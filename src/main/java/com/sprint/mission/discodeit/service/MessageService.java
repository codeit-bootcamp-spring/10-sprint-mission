package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.*;

public interface MessageService {
    Message create(String msg);

    Optional<Message> read(UUID id);

    ArrayList<Message> readAll();

    Message update(UUID id, String messageData);

    void delete(UUID id);
}
