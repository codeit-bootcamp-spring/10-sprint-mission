package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
    private final List<Message> data;

    public JCFMessageService() {
        this.data = new ArrayList<>();
    }

    @Override
    public Message create(Channel channel, User sender, String text) {
        Message message = new Message(channel, sender, text);
        data.add(message);
        return message;
    }

    @Override
    public Message read(UUID id) {
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(data);
    }

    @Override
    public List<Message> readAllMessageByChannel(Channel channel) {
        return data.stream()
                .filter(message -> message.getChannel().equals(channel))
                .collect(Collectors.toList());
    }

    @Override
    public Message update(UUID id, String text) {
        Message message = read(id);
        return message.update(text);
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(message -> message.getId().equals(id));
    }

}




