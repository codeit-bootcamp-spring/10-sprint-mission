package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageRepository messageRepo;

    public FileMessageService(UserService userService, ChannelService channelService, MessageRepository messageRepo) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageRepo = messageRepo;
    }

    public Message createMessage(UUID userId, UUID channelId, String content) {
        User user =  userService.findUserById(userId);
        Channel channel = channelService.findChannelById(channelId); // 이미 find단계에서 유저서비스나 채널서비스가 예외던지기 검증을 한다

        Message newMessage = new Message(userId, channelId, content);
        channelService.addMessage(channelId, newMessage.getId());
        messageRepo.save(newMessage);
        return newMessage;
    }

    public Message updateMessage(UUID messageId, String content) {
        Message message = findMessageById(messageId);

        if(message.getContent().equals(content)){
            throw new IllegalArgumentException("수정 전 메세지와 같습니다");
        }

        message.setContent(content);
        message.setUpdatedAt(System.currentTimeMillis());
        messageRepo.save(message);
        return message;
    }

    public Message findMessageById(UUID messageId) {
        return messageRepo.findById(messageId);
    }

    public List<Message> findMessageByUserId(UUID userId){
        userService.findUserById(userId);
        return messageRepo.findByUserId(userId);
    }

    public List<Message> findMessageByChannelId(UUID channelId) {
        channelService.findChannelById(channelId);
        return messageRepo.findByChannelId(channelId);
    }

    public void deleteMessage(UUID messageId){
        Message message = findMessageById(messageId);
        channelService.removeMessage(message.getChannelId(), messageId);
        messageRepo.deleteById(messageId);
    }

}
