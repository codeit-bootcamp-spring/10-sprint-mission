package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;

import java.util.*;
public class JCFMessageService implements MessageService{
    private final Map<UUID, Message> data;

    //의존성 주입
    // 서비스마다 자기 이외의 서비스 객체 만들어... 의존성 주입..?
    private final UserService userService;
    private final ChannelService channelService;


    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;

    }
    // 저 위에 선언하고 생성자를 굳이 분리하는 이유는????????? -< 물어보가ㅣ
    @Override
    public void createMessage(Message message){
        // 연관된 도메인 유효성 검증
        // 메세지 객체를 설정할 때 Message m1 = new Message("안녕하세요!", u1, ch1);
        if (message.getSender()==null || message.getChannel()==null){
            throw new IllegalArgumentException("메세지 생성 실패: sender나 channel이 null 입니다.");
        }
        //User 존재 여부 확인
        UUID senderId = message.getSender().getId();
        if(userService.getUserByID(senderId) == null){
            throw new IllegalArgumentException("메세지 생성 실패: 존재하지 않는 사용자입니다. ID: " + senderId );
        }
        //Channel 존재 여부 확인
        UUID channelId = message.getChannel().getId(); // Channel의 아이디.
        if(channelService.getChannelById(channelId)==null){
            throw  new IllegalArgumentException("메세지 생성 실패: 존재하지 않는 채널입니다. ID: " + senderId);
        }
        // 통과 시 저장.
        data.put(message.getId(), message);
    }

    @Override
    public Message getMessageById(UUID id){
        Message message = data.get(id); // 특정 id의 메세지 객체 찾기 (시스템)
        if(message == null) {
            throw new NoSuchElementException("해당 ID의 메세지가 존재하지 않습니다: " + id);
        }
        return message; //해쉬맵에서 키값으로 값 찾기.
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
        if(existing == null) {
            throw new NoSuchElementException("수정할 메세지가 존재하지 않습니다: " + message.getId());
        } existing.update(message.getContent());
    }

    //삭제시 유효성 검증 필요!!!
    @Override
    public void deleteMessage(UUID id) {
        if(!data.containsKey(id)) {
            throw new NoSuchElementException("삭제할 메세지가 존재하지 않습니다: " + id);
        }
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
