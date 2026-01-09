package com.sprint.mission.service.jcf;

import com.sprint.mission.entity.Message;
import com.sprint.mission.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messages;


    public JCFMessageService(Map<UUID, Message> messages) {
        this.messages = messages;
    }

    @Override
    public void createMessage(String content) {
        Message message = new Message(content);
        messages.put(message.getId(), message);
    }

    @Override
    public Message findById(UUID id) {
        validateMessageExists(id);
        return messages.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public void updateMessage(UUID id, String content) {
        Message message = findById(id);
        message.updateContent(content);
    }

    @Override
    public void deleteById(UUID id) {
        validateMessageExists(id);
        messages.remove(id);
    }

    private void validateMessageExists(UUID id) {
        if (!messages.containsKey(id)) {
            throw new IllegalArgumentException("메시지가 존재하지 않습니다.");
        }
    }
}
