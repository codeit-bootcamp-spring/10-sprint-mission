package com.sprint.mission.descodeit.service.jcf;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;
import com.sprint.mission.descodeit.service.ChannelService;
import com.sprint.mission.descodeit.service.MessageService;
import com.sprint.mission.descodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private UserService userService;
    private ChannelService channelService;

    public JCFMessageService(){
        this.data = new HashMap<>();
    }

    public void setDependencies(UserService userService, ChannelService channelService){
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(UUID userId, String text, UUID channelId) {
        User user = userService.findUser(userId);
        Channel channel = channelService.findChannel(channelId);
        // 메시지 객체 생성
        Message message = new Message(user, text, channel);
        // 데이터에 객체 추가
        data.put(message.getId(), message);

        return message;
    }

    @Override
    public Message findMessage(UUID messageId) {
        Message message = Optional.ofNullable(data.get(messageId))
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
    public Message update(UUID messageId, String newText) {
        Message message = findMessage(messageId);
        message.updateMessage(newText);
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        Message message = data.get(messageId);

        // 데이터가 없으면 예외를 던지는 대신 false를 즉시 리턴
        if (message == null) {
            return;
        }

        // 해당 메시지가 속했던 유저와 채널에서 메시지 정보 삭제
        User user = message.getUser();
        user.getMessageList().remove(message);

        Channel channel = message.getChannel();
        channel.getMessageList().remove(message);

        //데이터에서도 삭제
        data.remove(messageId);
    }
}
