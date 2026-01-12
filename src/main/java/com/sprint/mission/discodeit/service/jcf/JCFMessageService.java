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
        if (message == null) {
            throw new IllegalArgumentException("생성하고자 하는 메시지가 null일 수 없음");
        }
        messages.put(message.getId(), message);
    }

    @Override
    public Message findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("찾고자 하는 메시지의 id가 null일 수 없음");
        }
        Message message = messages.get(id);
        if (message == null) {
            throw new IllegalStateException("해당 id의 메시지를 찾을 수 없음");
        }
        return message;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public void updateById(UUID id, String content) {
        Message targetMessage = findById(id);
        if (content == null) {
            throw new IllegalArgumentException("업데이트하려는 메시지 내용이 null일 수 없음");
        }
        targetMessage.updateContent(content);
    }

    @Override
    public void deleteById(UUID id) {
        findById(id);
        messages.remove(id);
    }

    @Override
    public void printAllMessages() {
        for (Message message : messages.values()) {
            System.out.println(message);
        }
    }
}
