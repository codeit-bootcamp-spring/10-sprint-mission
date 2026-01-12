package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashSet;
import java.util.UUID;

public class JCFMessage implements MessageService {
    private static JCFMessage instance = null;
    private JCFMessage(){}
    public static JCFMessage getInstance(){
        if(instance == null){
            instance = new JCFMessage();
        }
        return instance;
    }

    HashSet<Message> messages = new HashSet<>();

    @Override
    public Message read(UUID id) {
        return messages.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public HashSet<Message> readAll() {
        return messages;
    }

    @Override
    public Message create(Message message) {
        messages.add(message);
        return message;
    }

    @Override
    public void delete(Message message) {
        messages.remove(message);
    }

    @Override
    public void update(UUID id, String msg) {
        messages.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .ifPresent(message -> message.updateMessage(msg));
    }
}
