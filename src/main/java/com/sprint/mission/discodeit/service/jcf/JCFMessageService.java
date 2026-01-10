package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messages = new HashMap<>();

    @Override
    public void sendMessage(UUID userId, String content) {
        Message message = new Message(userId, content);
        // 메시지 생성 및 리스트에 추가
        messages.put(message.getId(), message);
    }

    @Override
    public List<Message> getAllMessages() {
        return messages.values().stream().toList();
    }

    @Override
    public List<Message> getMessageListByUser(UUID userId) {
        return messages.values().stream()
                .filter(id -> id.getSentUserId().equals(userId))
                .toList();
    }

    @Override
    public Optional<Message> getMessageById(UUID messageId) {
        return messages.values().stream()
                .filter(id -> id.getId().equals(messageId))
                .findFirst();
    }

    @Override
    public void editMessage(UUID messageId, String newContent) {
        messages.values().stream()
                .filter(id -> id.getId().equals(messageId))
                .findFirst()
                .ifPresent(msg -> msg.updateContent(newContent));
    }

    @Override
    public void deleteMessage(UUID messageId) {
        messages.values().stream()
                .filter(id -> id.getId().equals(messageId))
                .findFirst()
                .ifPresent(msg -> messages.remove(messageId));
    }
}
