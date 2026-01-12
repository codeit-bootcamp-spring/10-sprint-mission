package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    ArrayList<Message> list = new ArrayList<>();

    @Override
    public Message createMessage(String content, UUID channelId, UUID userId) {
        Message message = new Message(content, channelId, userId);
        list.add(message);
        return message;
    }

    @Override
    public Message readMessage(UUID id) {
        for (Message message : list) {
            if (id.equals(message.getId())){
                return message;
            }
        }
        return null;
    }

    @Override
    public List<Message> readAllMessage() {
        return list;
    }


}
