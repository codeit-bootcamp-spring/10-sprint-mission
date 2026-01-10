package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;

    private static JCFMessageService instance = null;

    public JCFMessageService() {
        data = new HashMap<>();
    }

    public static JCFMessageService getInstance() {
        if (instance == null) {
            instance = new JCFMessageService();
        }
        return instance;
    }

    @Override
    public UUID addMessage(User user, Channel channel, String text) {
        Message message = new Message(user, channel, text);
        UUID id = message.getId();
        data.put(id, message);

        return id;
    }

    @Override
    public Message getMessage(UUID id) {
        return data.get(id);
    }

    @Override
    public Message getMessage(String text) {
        Optional<Map.Entry<UUID, Message>> result = data.entrySet().stream()
                .filter(entry -> entry.getValue()
                        .getText()
                        .contains(text))
                .findFirst();
        return result.map(Map.Entry::getValue).orElse(null);
    }

    @Override
    public Iterable<Message> getUserMessages(User user) {
        Iterable<Message> result = () -> data.values().stream()
                .filter(m -> m.getUser().equals(user))
                .sorted(Comparator.comparingLong(Message::getCreatedAt))
                .iterator();
        return result;
    }

    // Problem: 정렬이 제대로 이루어지지 않음. (HashMap의 특성?)
    @Override
    public Iterable<Message> getAllMessages() {
        return () -> data.values().stream()
                .sorted(Comparator.comparingLong(Message::getCreatedAt))
                .iterator();
    }

    @Override
    public void updateMessage(UUID id, String text) {
        Message message = getMessage(id);
        if (message == null) {
            throw new NotFoundException("Message " + id + "을 찾을 수 없었습니다.");
        }

        message.updateText(text);
    }

    @Override
    public void deleteMessage(UUID id) {
        if (!data.containsKey(id)) {
            throw new NotFoundException("Message " + id + "는 이미 삭제되었거나 없는 message입니다.");
        }

        data.remove(id);
    }
}
