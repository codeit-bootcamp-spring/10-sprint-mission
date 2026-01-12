package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import javax.swing.event.ChangeEvent;
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
    public Message create(String contents, User sender, Channel channel) {
        // msg에 sender, channel 존재하는 거 check
        if (sender == null | channel == null){
            throw new IllegalArgumentException("Not found");
        }

        // channel 안에 sender 존재하는지 check 후에 create
        Message msg = new Message(contents, sender, channel);
        messageData.put(msg.getId(), msg);

        // sender, channel에 message 할당
        sender.addMessage(msg);
        channel.addMessage(msg);
        return msg;
    }

    @Override
    public Message read(UUID messageID) {
        if (messageID == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        return messageData.get(messageID);
    }

    @Override
    public List<Message> readAll() {
        return messageData.values().stream().toList();
    }

    @Override
    public void update(UUID messageID, String contents) {
        if (messageID == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        Message msg = read(messageID);
        msg.updateContents(contents);
    }

    @Override
    public void delete(UUID messageID) {
        if (messageID == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        Message msg = messageData.get(messageID);
        User sender = msg.getSender();
        Channel channel = msg.getChannel();

        // sender, channel에서 msg 삭제
        sender.removeMessage(msg);
        channel.removeMessage(msg);
        messageData.remove(messageID);

    }
}
