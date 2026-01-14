package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.utils.Validation;

import java.util.*;

public class JCFMessageService implements MessageService{

    private final Map<UUID, Message> data;

    // 서비스마다 자기 이외의 서비스 객체 만들어... 의존성 주입..
    private final UserService userService;
    private final ChannelService channelService;


    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(String content, UUID senderId, UUID channelId) {
        //  입력값 검증
        Validation.notBlank(content, "메세지 내용");

        if (senderId == null || channelId == null) {
            throw new IllegalArgumentException("senderId나 channelId가 null일 수 없습니다.");
        }

        // User 유효성 검사 및 조회
        User sender = userService.findUserOrThrow(senderId);

        // Channel 유효성 검사 및 조회
        Channel channel = channelService.findChannelOrThrow(channelId);

        //  Message 생성 및 저장
        Message message = new Message(content, sender, channel);
        data.put(message.getId(), message);

        return message;
    }

    @Override
    public List<Message> getMessageAll() {
        return new ArrayList<>(data.values());
    }
    @Override
    public Message getMessageById(UUID id) {
        return data.values().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(()->new NoSuchElementException("해당 Id의 메세지는 존재하지 않습니다: "+ id ));
    }
    //data.values()가 반환하는 Collection은 Map과 연결된 “뷰(View)” 객체라서,
    //그냥 그걸 리턴하면 이후에 외부 코드가 실수로 리스트를 건드렸을 때
    //Map 내부 데이터에도 영향을 줄 수 있다.

    //메세지 내용 수정기능
    @Override
    public Message updateMessage(UUID uuid, String newContent){
        Message existing = findmsgOrThrow(uuid);
        existing.update(newContent);
        return existing;
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
        return data.values().stream()
                .filter(m -> m.getChannel() != null
                && m.getChannel().getChannelName().equals(channelName))
                .toList();
    }

    // 이름 중복 허용인데...??? -> 그럼 id로 찾도록 (관리자용)
    // (사용자용)은 별명이나 유저이름으로 조회하도록..?
    @Override
    public List<Message> getMessagesBySenderId(UUID senderId) {
        return data.values().stream()
                .filter(m -> m.getSender() != null
                        && m.getSender().getId().equals(senderId))
                .toList();
    }
    // ID로 메세지를 찾고, 없으면 예외 던짐.
    public Message findmsgOrThrow(UUID id) {
        Message message  = data.get(id);
        if (message == null) {
            throw new NoSuchElementException("해당 메세지가 존재하지 않습니다: " + id);
        }
        return message;
    }


}
