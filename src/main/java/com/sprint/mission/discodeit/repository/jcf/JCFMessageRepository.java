package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID,Message> data = new HashMap<>();

    @Override
    public Message save(Message message) {
        data.put(message.getId(),message);

        return data.get(message.getId());
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
    public void delete(UUID messageId) {
        data.remove(messageId);

    }
}
