package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.UUID;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JCFMessageService implements MessageService{
    private final Map<UUID, Message> data;

    public JCFMessageService(){
        this.data = new HashMap<>();
    }

    @Override
    public void create(Message message){
        data.put(message.getId(), message);
    }

    @Override
    public Message readById(UUID id){
        return data.get(id);
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(Message message) {
        Message existingMessage = data.get(message.getId());

        if (existingMessage != null){
            existingMessage.update(message.getContent());
        }
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
