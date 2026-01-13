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

    public void createMessage(Message message){
        if(findId(message.getId()) != null){
            System.out.println("이미 있는 메세지입니다.");
            return;
        }
        data.add(message);
        System.out.println("메세지 생성이 완료되었습니다.");
    }

    public Message findId(UUID id){
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Message> findAll(){
        return data;
    }

    public List<Message> findByChannelId(UUID channelId){
        return data.stream()
                .filter(msg -> msg.getChannelId().equals(channelId)) // 조건: 채널 ID가 같은가?
                .toList();
    }

    public void update(Message msg, String content){
        if(findId(msg.getId()) == null){
            System.out.println("존재하지 않는 메세지입니다.");
            return;
        }
        msg.setContent(content);
    }

    public void delete(UUID id){
        Message target = findId(id);
        data.remove(target);
    }
    public void deleteByChannelId(UUID channelId){
        List<Message> temp = findByChannelId(channelId);
        temp.stream()
                .forEach(message -> delete(message.getId()));
    }

}
