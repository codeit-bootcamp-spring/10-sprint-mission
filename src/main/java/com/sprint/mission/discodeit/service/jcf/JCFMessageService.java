package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, List<Message>> data;        // key: ChannelUuid

    public JCFMessageService() {
        data = new HashMap<>();
    }

    @Override
    public Message createMessage(UUID channelId, UUID userId, String message) {
        Message msg = new Message(channelId, userId, message);
        data.computeIfAbsent(channelId, k -> new ArrayList<>()).add(msg);
        return msg;
    }

    @Override
    public Optional<Message> findMessage(UUID uuid) {
        return findAllMessages().stream()
                .filter(m -> Objects.equals(m.getId(), uuid)).findFirst();
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID uuid) {
        return data.computeIfAbsent(uuid, k -> new ArrayList<>());
    }

    @Override
    public List<Message> findAllMessages() {
        return data.values().stream().flatMap(List::stream).toList();
    }

    @Override
    public Message updateMessage(UUID uuid, String newMessage) {
        Message msg = findMessage(uuid).orElseThrow(() -> new IllegalStateException("존재하지 않는 메시지입니다"));

        if (!Objects.equals(msg.getMessage(), newMessage)) {
            msg.updateMessage(newMessage);
            msg.updateUpdatedAt();
        }

        return msg;
    }

    @Override
    public void deleteMessage(UUID uuid) {
        Message msg = findMessage(uuid).orElseThrow(() -> new IllegalStateException("존재하지 않는 메시지입니다"));
        for (var list : data.values()) {
            if (list.remove(msg)) {
                return;
            }
        }
    }
}
