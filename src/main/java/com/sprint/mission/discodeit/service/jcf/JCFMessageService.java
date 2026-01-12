package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messages;

    public JCFMessageService() {
        messages = new HashMap<>();
    }
    @Override
    public void createMessage(Message message) {
        messages.put(message.getId(), message);
    }

    @Override
    public Message findById(UUID id) {
        return messages.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public void updateById(UUID id, String content) {
        Message targetMessage = findById(id);
        targetMessage.updateContent(content);
    }

    @Override
    public void deleteById(UUID id) {
        messages.remove(id);

    }

    @Override
    public void printAllMessages() {
        for (Message message : messages.values()) {
            System.out.println(message);
        }
    }
}
