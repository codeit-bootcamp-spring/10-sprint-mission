package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageData;

    public JCFMessageService() {
        this.messageData = new HashMap<>();
    }

    @Override
    public Message create(String contents, UUID senderID, UUID channelID) {
        // msg에 sender가 존재하는 거 check
        // Channel 존재 확인 check
        // channel 안에 sender 존재하는지 check 후에 create
        Message msg = new Message(contents, senderID, channelID);
        messageData.put(msg.getId(), msg);
        return msg;
    }

    @Override
    public Message read(UUID messageID) {
        return messageData.get(messageID);
    }

    @Override
    public List<Message> readAll() {
        return messageData.values().stream().toList();
    }

    @Override
    public void update(UUID messageID, String contents) {
        Message msg = read(messageID);
        msg.updateContents(contents);
    }

    @Override
    public void delete(UUID id) {
        messageData.remove(id);
    }
}
