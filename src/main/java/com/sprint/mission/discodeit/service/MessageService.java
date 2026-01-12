package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.HashSet;
import java.util.UUID;

public interface MessageService {
    public Message find(UUID id);
    public HashSet<Message> findAll();
    public Message create(Message message);
    public void delete(UUID id);
    public void update(UUID id, String str);
}
