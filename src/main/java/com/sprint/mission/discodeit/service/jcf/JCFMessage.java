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
    public void read(UUID id) {
        for(Message message : messages) {
            if (message.getId() == id) {
                System.out.println(message);
                return;
            }
        }
    }

    @Override
    public void readAll() {
        messages.forEach(System.out::println);
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
    public void update(Message originmessage, Message aftermessage) {
        if(originmessage == null || originmessage.getId() == null) {
            return;
        }

        for(Message msg : messages) {
            if (msg.getId() == originmessage.getId()){
                msg.updateMessage(aftermessage.getMessage());
                return;
            }
        }
    }
}
