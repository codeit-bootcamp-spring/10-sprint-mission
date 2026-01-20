package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    final Map<UUID, Message> messageData;

    public JCFMessageRepository() {
        messageData = new HashMap<>();
    }

    @Override
    public Message find(UUID messageID) {
        Message message = messageData.get(messageID);
        if (message == null) {
            throw new IllegalArgumentException("Message Not Found: "+messageID);
        }
        return message;
    }

    @Override
    public List<Message> findAll() {
        return messageData.values().stream().toList();
    }

    @Override
    public void deleteMessage(Message message) {
        messageData.remove(message.getId());
    }

    @Override
    public Message save(Message message){
        messageData.put(message.getId(), message);
        return message;
    }
}
