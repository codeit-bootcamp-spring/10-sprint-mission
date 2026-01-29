package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class BasicMessageService implements MessageService {
    private MessageRepository messageRepository;
    private BasicUserService userService;
    private BasicChannelService channelService;

    public BasicMessageService(MessageRepository messageRepository, BasicChannelService channelService, BasicUserService userService) {
        this.messageRepository = messageRepository;
        this.channelService = channelService;
        this.userService = userService;
    }

    @Override
    public Message create(String msg, UUID userId, UUID channelId) {
        User user = userService.read(userId);
        Channel channel = channelService.read(channelId);
        Message message = new Message(msg, user, channel);
        this.messageRepository.save(message);
        return message;
    }

    @Override
    public Message read(UUID id) {
        for (Message message : this.messageRepository.loadAll()) {
            if (message.getId().equals(id)) {
                return message;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public List<Message> readAll() {
        return this.messageRepository.loadAll();
    }

    @Override
    public Message updateMessageData(UUID id, String messageData) {
        this.messageRepository.updateMessageData(id, messageData);
        return this.read(id);
    }

    @Override
    public void delete(UUID id) {
        this.messageRepository.delete(id);
    }

    // 특정 유저가 발행한 메시지 리스트 조회
    @Override
    public List<Message> readUserMessageList(UUID userId) {
        User user = userService.read(userId);
        return user.getMessageList();
    }

    // 특정 채널의 발행된 메시지 목록 조회
    @Override
    public List<Message> readChannelMessageList(UUID channelId) {
        Channel channel = channelService.read(channelId);
        return channel.getMessagesList();
    }
}
