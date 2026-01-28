package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepo;
    private final UserService userService;
    private final ChannelService channelService;

    public BasicMessageService(MessageRepository messageRepo, UserService userService, ChannelService channelService) {
        this.messageRepo = messageRepo;
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        userService.findUserById(userId);
        channelService.findChannelById(channelId);

        Message newMessage = new Message(userId, channelId, content);
        channelService.addMessage(channelId, newMessage.getId());
        messageRepo.save(newMessage);
        return newMessage;
    }

    @Override
    public Message findMessageById(UUID messageId) {
        Message message = messageRepo.findById(messageId);
        if(message == null){
            throw new IllegalArgumentException("해당 메세지를 찾을 수 없습니다");
        }
        return message;
    }

    @Override
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

    @Override
    public List<Message> findMessageByChannelId(UUID channelId) {
        channelService.findChannelById(channelId);
        return messageRepo.findByChannelId(channelId);
    }

    @Override
    public List<Message> findMessageByUserId(UUID userId) {
        userService.findUserById(userId);
        return messageRepo.findByUserId(userId);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = findMessageById(messageId);
        channelService.removeMessage(message.getChannelId(), messageId);
        messageRepo.deleteById(messageId);
    }
}
