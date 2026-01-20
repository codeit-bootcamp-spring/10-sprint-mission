package com.sprint.mission.service.basic;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.entity.Message;
import com.sprint.mission.entity.User;
import com.sprint.mission.repository.MessageRepository;
import com.sprint.mission.service.ChannelService;
import com.sprint.mission.service.MessageService;
import com.sprint.mission.service.UserService;

import java.util.List;
import java.util.UUID;

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
    public Message create(UUID userId, UUID channelId, String content) {
        User user = userService.getUserOrThrow(userId);
        Channel channel = channelService.getChannelOrThrow(channelId);
        channel.validateMember(user);

        return messageRepository.save(new Message(user, channel, content));
    }

    @Override
    public Message update(UUID userId, UUID messageId, String content) {
        User user = userService.getUserOrThrow(userId);
        Message message = getMessageOrThrow(messageId);
        message.validateMessageOwner(user);
        message.updateContent(content);

        return messageRepository.save(message);
    }

    @Override
    public Message getMessageOrThrow(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메시지가 존재하지 않습니다."));
    }

    @Override
    public List<Message> getAllMessage() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> getMessagesInChannel(UUID channelId) {
        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public List<Message> getMessagesByUserInChannel(UUID userId, UUID channelId) {
        return messageRepository.findByUserIdAndChannelId(userId, channelId);
    }

    @Override
    public void deleteMessage(UUID userId, UUID messageId) {
        User user = userService.getUserOrThrow(userId);
        Message message = getMessageOrThrow(messageId);
        message.validateMessageOwner(user);

        messageRepository.deleteById(messageId);
    }
}
