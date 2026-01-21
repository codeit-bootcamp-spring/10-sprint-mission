package com.sprint.mission.descodeit.repository.jcf;

import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;

    public JCFMessageRepository(){
        data = new HashMap<>();
    }

    @Override
    public Message findById(UUID messageId) {
        return data.get(messageId);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void save(UUID messageId, Message message) {
        data.put(messageId, message);
    }

    @Override
    public void delete(UUID messageId) {
        data.remove(messageId);
    }
}
