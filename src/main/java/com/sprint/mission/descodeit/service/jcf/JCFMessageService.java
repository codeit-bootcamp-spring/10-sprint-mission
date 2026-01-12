package com.sprint.mission.descodeit.service.jcf;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;
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
    public Message findMessage(UUID messageID) {
        Message message = Optional.ofNullable(data.get(messageID))
                .orElseThrow(()-> new NoSuchElementException("해당 메시지를 찾을 수 없습니다"));
        return message;
    }

    @Override
    public List<Message> findAllMessages() {
        System.out.println("[메세지 전체 조회]");
        for(UUID id : data.keySet()){
            System.out.println(data.get(id));
        }
        System.out.println();
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID messageID, String newText) {
        Message message = findMessage(messageID);
        message.updateMessage(newText);
        return message;
    }

    @Override
    public boolean delete(UUID messageID) {
        Message message = data.get(messageID);

        // 데이터가 없으면 예외를 던지는 대신 false를 즉시 리턴
        if (message == null) {
            return false;
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
