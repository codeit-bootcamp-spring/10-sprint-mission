
package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final List<Message> data;

    public JCFMessageService() {
        this.data = new ArrayList<>();
    }

    @Override
    public Message create(Message message) {
        data.add(message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        for (Message message : data) {
            if (message.getId().equals(id)) {
                return message;
            }
        }
        return null;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public Message update(UUID id, String content, UUID userId, UUID channelId) {
        for (Message message : data) {
            if (message.getId().equals(id)) {
                message.update(content, userId, channelId); // Message 엔티티의 update 메서드
                return message;
            }
        }
        throw new IllegalArgumentException("Message not found: " + id);
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(message -> message.getId().equals(id));
    }
}
