package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;

import java.util.*;
public class JCFMessageService implements MessageService{
    private final Map<UUID, Message> data;

    public JCFMessageService() {
        this.data = new HashMap<>();
    }
    // 저 위에 선언ㄴ하고 생성자를 굳이 분리하는 이유는????????? -< 물어보가ㅣ
    @Override
    public void createMessage(Message message){
        data.put(message.getId(), message);
    }
    @Override
    public Message getMessageById(UUID id){
        return data.get(id); //해쉬맵에서 키값으로 값 찾기.
    }
    @Override
    public List<Message> getMessageAll() {
        return new ArrayList<>(data.values());
    }
    //data.values()가 반환하는 Collection은 Map과 연결된 “뷰(View)” 객체라서,
    //그냥 그걸 리턴하면 이후에 외부 코드가 실수로 리스트를 건드렸을 때
    //Map 내부 데이터에도 영향을 줄 수 있어.\

    //메세지 내용 수정기능..
    @Override
    public void updateMessage(Message message){
        Message existing = data.get(message.getId());
        if(existing != null) {
            existing.update(message.getContent());
        }
    }
    @Override
    public void deleteMessage(UUID id) {
        data.remove(id);
    }
    @Override
    public List<Message> getMessagesByChannelName(String channelName) {
        List<Message> result = new ArrayList<>();
        for (Message message : data.values()) {
            if (message.getChannel() != null &&
                    message.getChannel().getChannelName() != null &&
                    message.getChannel().getChannelName().trim().equalsIgnoreCase(channelName.trim())) {
                result.add(message);
            }
        }
        return result;
    }
    @Override
    public List<Message> getMessagesBySenderName(String senderName) {
        List<Message> result = new ArrayList<>();
        for(Message message: data.values()){
            if(message.getSender() != null &&
            message.getSender().getUserName().equals(senderName)){
                result.add(message);
            }
        }
        return result;
    }
}
