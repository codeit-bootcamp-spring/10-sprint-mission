package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    // 필드
    private final Map<UUID, Message> messageData;
    // 생성자
    public JCFMessageService() {
        this.messageData = new HashMap<>();
    }

    //생성
    @Override
    public Message create(String contents, User sender, Channel channel) {
        // sender, channel 존재하는 지 check
        if (sender == null | channel == null){
            throw new IllegalArgumentException("Not found");
        }

        // create
        Message msg = new Message(contents, sender, channel);
        messageData.put(msg.getId(), msg);

        // sender, channel에 message 할당
        sender.addMessage(msg);
        channel.addMessage(msg);
        return msg;
    }
    // 조회
    @Override
    public Message find(UUID messageID) {
        Message message = messageData.get(messageID);

        if (message == null){
            throw new IllegalArgumentException("Message Not Found: "+messageID);
        }

        return message;
    }
    // 전체 조회
    @Override
    public List<Message> readAll() {
        return messageData.values().stream().toList();
    }

    // 수정
    @Override
    public void update(UUID messageID, String contents) {
        if (messageID == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        Message msg = find(messageID);
        msg.updateContents(contents);
    }

    // 삭제
    @Override
    public void delete(UUID messageID) {
        Message msg = find(messageID);
        User sender = msg.getSender();
        Channel channel = msg.getChannel();

        // sender, channel에서 msg 삭제
        sender.removeMessage(msg);
        channel.removeMessage(msg);
        messageData.remove(messageID);
    }
}
