package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    public void save(Message message);
    public Message findByID(UUID uuid);
    public List<Message> findAll();

    public Message delete(Message message);
}
