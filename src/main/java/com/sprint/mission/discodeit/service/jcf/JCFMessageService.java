package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashSet;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private static JCFMessageService instance = null;
    private JCFMessageService(){}
    public static JCFMessageService getInstance(){
        if(instance == null){
            instance = new JCFMessageService();
        }
        return instance;
    }

    HashSet<Message> messages = new HashSet<>();

    @Override
    public Message find(UUID id) {
        return messages.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Channel not found: id = " + id));
    }

    @Override
    public HashSet<Message> findAll() {
        HashSet<Message> newMessages = new HashSet<>();
        for(Message message : messages){
            newMessages.add(message);
        }
        return newMessages;
    }

    @Override
    public Message create(Message message) {
        messages.add(message);
        return message;
    }

    @Override
    public void delete(UUID id) {
        messages.remove(find(id));
    }

    @Override
    public Message update(UUID id, String msg) {
        this.find(id)
                .updateMessage(msg);
        return this.find(id);
    }
}
