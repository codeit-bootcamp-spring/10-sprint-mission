package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;

    public JCFMessageService() {
        data = new HashMap<>();
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
    public Iterable<Message> getAllMessages() {
        return data.values();
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
