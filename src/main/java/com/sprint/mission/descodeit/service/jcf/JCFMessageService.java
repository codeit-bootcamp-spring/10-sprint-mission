package com.sprint.mission.descodeit.service.jcf;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;
import com.sprint.mission.descodeit.service.ChannelService;
import com.sprint.mission.descodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;

    public JCFMessageService(){
        this.data = new HashMap<>();
    }

    @Override
    public Message create(User user, String text, Channel channel) {
        // 메시지 객체 생성
        Message message = new Message(user, text, channel);
        // 데이터에 객체 추가
        data.put(message.getId(), message);

        // 채널과 유저 객체에 해당 메시지 객체를 저장
        user.addMessage(message);
        channel.addMessage(message);

        return message;
    }

    @Override
    public Message findMessages(UUID messageID) {
        Message message = data.get(messageID);
        if(message == null){
            throw new NoSuchElementException();
        }
        System.out.println(message);
        return message;
    }

    @Override
    public List<Message> findAllMessages() {
        System.out.println("[메세지 전체 조회]");
        if(data.isEmpty()){
            System.out.println("조회할 메시지가 없습니다");
            return new ArrayList<>();
        }

        for(UUID id : data.keySet()){
            System.out.println(data.get(id));
        }
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID messageID, String newText) {
        Message message = data.get(messageID);

        if(message == null){
            throw new NoSuchElementException();
        }
        message.updateMessage(newText);
        return message;
    }

    @Override
    public boolean delete(UUID messageID) {
        Message message = data.get(messageID);
        if(message == null){
            throw new NoSuchElementException();
        }
        // 해당 메시지가 속했던 유저와 채널에서 메시지 정보 삭제
        User user = message.getUser();
        user.getMessageList().remove(message);

        Channel channel = message.getChannel();
        channel.getMessageList().remove(message);

        //데이터에서도 삭제
        data.remove(messageID);

        return true;
    }
}
