package com.sprint.mission.service.jcf;

import com.sprint.mission.entity.Message;
import com.sprint.mission.service.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;

    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    @Override
    public Message create(String message) {
        return create(message, data, Message::new);
    }

    @Override
    public Message get(UUID uuid) {
        return get(uuid, data);
    }

    @Override
    public List<Message> getAll(List<UUID> uuids) {
        return getAll(uuids, data);
    }

    @Override
    public void update(UUID uuid, String newMessage) {
        update(uuid, newMessage, data);
    }

    @Override
    public void delete(UUID uuid) {
        delete(uuid, data);
    }
}
