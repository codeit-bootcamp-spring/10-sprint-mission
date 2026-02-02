package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "jcf")
public class JCFMessageRepository implements MessageRepository {
    final Map<UUID, Message> messageData;

    public JCFMessageRepository() {
        messageData = new HashMap<>();
    }

    @Override
    public Optional<Message> find(UUID messageID) {
        return Optional.ofNullable(messageData.get(messageID));
    }

    @Override
    public List<Message> findAll() {
        return messageData.values().stream().toList();
    }

    @Override
    public void deleteMessage(UUID messageID) {
        messageData.remove(messageID);
    }

    @Override
    public Message save(Message message){
        messageData.put(message.getId(), message);
        return message;
    }
}
