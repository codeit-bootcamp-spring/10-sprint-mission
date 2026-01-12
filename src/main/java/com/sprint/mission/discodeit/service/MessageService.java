package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.HashSet;
import java.util.UUID;

public interface MessageService {
    public Message read(UUID id);
    public HashSet<Message> readAll();
    public Message create(Message message);
    public void delete(Message message);
    public void update(UUID id, String str);
}
