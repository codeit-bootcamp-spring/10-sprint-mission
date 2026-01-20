package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.Set;
import java.util.UUID;

public interface MessageService {
    public Message find(UUID id);
    public Set<Message> findAll();
    public Message create(UUID userID, String msg, UUID channelID);
    public void delete(UUID id, UUID userID);
    public Message update(UUID id, String msg);
    public Message update(Message message);
}
