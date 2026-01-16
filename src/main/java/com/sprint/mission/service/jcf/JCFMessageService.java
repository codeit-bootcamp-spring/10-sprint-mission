package com.sprint.mission.service.jcf;

import com.sprint.mission.entity.Message;
import com.sprint.mission.service.MessageService;

import java.util.Map;
import java.util.UUID;

public class JCFMessageService extends JCFBaseService<Message> implements MessageService {
    public JCFMessageService() {
        super();
    }

    public JCFMessageService(Map<UUID, Message> data) {
        super(data);
    }

    @Override
    public Message create(String content, UUID userId, UUID channelId) {
        Message message = new Message(content, userId, channelId);
        getData().put(message.getId(), message);
        return message;
    }
}
