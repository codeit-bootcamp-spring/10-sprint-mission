package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    List<Message> data;

    public JCFMessageService(){
        data = new ArrayList<>();
    }

    public Message createMessage(String content, User sender, Channel channel){
        // user, channel 둘다 예외처리해야함.

        Message message = new Message(content, sender, channel);
        data.add(message);
        System.out.println("메세지 생성이 완료되었습니다.");
        return message;
    }

    public Message findId(UUID msgId){
        return data.stream()
                .filter(message -> message.getId().equals(msgId))
                .findFirst()
                .orElse(null);
    }

    public List<Message> findAll(){
        return data;
    }

    public List<Message> findByChannelId(UUID channelId){
        return data.stream()
                .filter(msg -> msg.getChannel().equals(channelId)) // 조건: 채널 ID가 같은가?
                .toList();
    }

    public Message update(UUID msgId, String content){
        Message foundMsg = findId(msgId);
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("내용이 비어있거나 공백입니다.");
        }
        return foundMsg;
    }

    public void delete(UUID msgId){
        Message target = findId(msgId);
        data.remove(target);
    }

}
