
package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final List<Message> data;

    public JCFMessageService() {
        this.data = new ArrayList<>();
    }

    @Override
    public Message createMessage(User user, Channel channel, String contents) {
        Message message = new Message(user, channel, contents);
        data.add(message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public Message update(UUID id, String content, User user, Channel channel) {
        for (Message message : data) {
            if (message.getId().equals(id)) {
                message.update(content, user, channel); // Message 엔티티의 update 메서드
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
