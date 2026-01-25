package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.*;

import java.util.*;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    public BasicMessageService(MessageRepository messageRepository, UserService userService, ChannelService channelService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(String content, UUID userId, UUID channelId) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        Objects.requireNonNull(content, "내용은 필수입니다.");
        if (!channel.getUsers().contains(user)) {
            throw new IllegalArgumentException("채널 멤버가 아닙니다.");
        }

        Message message = new Message(content, user, channel);
        messageRepository.save(message);
        channel.addMessage(message);
        channelService.update(channel.getId(), null, null);

        return message;
    }

    @Override
    public Message findById(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("메시지를 찾을 수 없습니다."));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        Channel channel = channelService.findById(channelId);
        return channel.getMessages();
    }

    @Override
    public Message update(UUID messageId, String content) {
        Message message = findById(messageId);
        Optional.ofNullable(content).ifPresent(message::updateContent);
        messageRepository.save(message);
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        Message message = findById(messageId);
        Channel channel = channelService.findById(message.getChannelId());
        channel.removeMessage(messageId);
        channelService.update(channel.getId(), null, null);
        messageRepository.delete(messageId);
    }

    @Override
    public List<Message> getMessageListByChannelId(UUID channelId) {
        return findAllByChannelId(channelId);
    }
}