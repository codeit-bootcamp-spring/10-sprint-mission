package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final HashMap<UUID, Message> data;

    public JCFMessageService() {
        this.data = new HashMap<>();
    }
    public JCFMessageService(Message message) {
        this.data = new HashMap<>();
        data.put(message.getId(), message);
    }

    @Override
    public void create() {
        Message message = new Message();
        data.put(message.getId(), message);
    }

    @Override
    public Optional<Message> read(UUID id) {
        if (data.isEmpty() || !data.containsKey(id)) {
            System.out.println("해당 메시지가 존재하지 않습니다.");
        }
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<ArrayList<Message>> readAll() {
        ArrayList<Message> list = new ArrayList<>(data.values());
        if (data.isEmpty()) {
            System.out.println("메시지 데이터가 존재하지 않습니다.");
        }
        return Optional.ofNullable(list);
    }

    @Override
    public void update(Message messageData) {
        this.data.put(messageData.getId(), messageData);
    }

    @Override
    public Message delete(UUID id) {
        return data.remove(id);
    }
}
