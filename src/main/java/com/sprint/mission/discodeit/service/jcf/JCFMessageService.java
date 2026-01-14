package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
    // 필드
    private final Map<UUID, Message> messageData;
    private ChannelService channelService;
    private UserService userService;

    // 생성자
    public JCFMessageService() {
        this.messageData = new HashMap<>();
    }
    // Setter
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    //생성
    @Override
    public Message create(String contents, UUID userID, UUID channelID) {
        // Service 예외
        if (userService == null) {
            throw new IllegalStateException("UserService is not set. Call setUserService() before using create().");
        }
        if (channelService == null) {
            throw new IllegalStateException("ChannelService is not set. Call setChannelService() before using create().");
        }

        // sender, channel 존재하는 지 check
        User sender = userService.find(userID);
        Channel channel = channelService.find(channelID);

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
    public List<Message> findAll() {
        return messageData.values().stream().toList();
    }

    // 수정
    @Override
    public Message update(UUID messageID, String contents) {
        if (messageID == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        Message msg = find(messageID);
        msg.updateContents(contents);
        return msg;
    }

    // 삭제
    @Override
    public void deleteMessage(UUID messageID) {
        Message msg = find(messageID);
        User sender = msg.getSender();
        Channel channel = msg.getChannel();

        // sender, channel의 messageList에서 msg 삭제
        sender.removeMessage(msg);
        channel.removeMessage(msg);

        // message 완전 삭제
        messageData.remove(messageID);
    }

    // channel 전체 메시지 조회
    public List<String> findMessagesByChannel (UUID channelID){
        // channelService 예외처리
        if (channelService == null) {
            throw new IllegalStateException("ChannelService is not set. Call setChannelService() before using create().");
        }

        Channel channel = channelService.find(channelID);
        return channel.getMessageList().stream()
                .map(Message::getContents)
                .collect(Collectors.toList());
    }

    // User 전체 메시지 조회
    public List<String> findMessagesByUser (UUID userID){
        // userService 예외처리
        if (userService == null) {
            throw new IllegalStateException("UserService is not set. Call setUserService() before using create().");
        }

        User user = userService.find(userID);
        return user.getMessageList().stream()
                .map(Message::getContents)
                .collect(Collectors.toList());
    }

}
