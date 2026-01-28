package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID,Message> messageStore;
    private final UserService userService;
    private final ChannelService channelService;


    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.messageStore = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        User user =  userService.findUserById(userId);
        Channel channel = channelService.findChannelById(channelId); // 이미 find단계에서 유저서비스나 채널서비스가 예외던지기 검증을 한다

        Message newMessage = new Message(userId, channelId, content);
        channelService.addMessage(channelId, newMessage.getId());
        messageStore.put(newMessage.getId(), newMessage);
        return newMessage;
    }

    public Message updateMessage(UUID messageId, String content) {
        Message message = findMessageById(messageId);

        if(message.getContent().equals(content)){
            throw new IllegalArgumentException("수정 전 메세지와 같습니다");
        }

        message.setContent(content);
        message.getUpdatedAt();
        return message;
    }

    @Override
    public Message findMessageById(UUID messageId) {
        Message message = messageStore.get(messageId);
        if(message == null){
            throw new IllegalArgumentException("해당 메세지를 찾을 수 없습니다");
        }

        return messageStore.get(messageId);
    }

    public List<Message> findMessageByUserId(UUID userId){
        User user =  userService.findUserById(userId);

        return messageStore.values().stream()
                .filter(message -> message.getUserId().equals(userId))
                .toList();
    }

    public List<Message> findMessageByChannelId(UUID channelId) {
        Channel channel = channelService.findChannelById(channelId);

        return messageStore.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    public void deleteMessage(UUID messageId){
        Message message = findMessageById(messageId);
        channelService.removeMessage(message.getChannelId(), messageId);
        messageStore.remove(messageId);
    }
}
