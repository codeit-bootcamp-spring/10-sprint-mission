package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelService channelService;
    private final UserService userService;

    public JCFMessageService(MessageRepository messageRepository, UserService userService, ChannelService channelService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(String content, UUID channelId, UUID userId) {
        Validators.validationMessage(content);
        Channel persistedChannel = channelService.find(channelId);
        User persistedUser = userService.find(userId);
        Message message = new Message(content, persistedChannel, persistedUser);

        persistedChannel.getMessages().add(message);
        persistedUser.getMessages().add(message);

        messageRepository.save(message);
        return message;
    }

    @Override
    public Message find(UUID id) {
        return validateExistenceMessage(id);
    }

    @Override
    public List<Message> findByChannelId() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = validateExistenceMessage(id);
        Optional.ofNullable(content)
                .ifPresent(cont -> {Validators.requireNotBlank(cont, "content");
            message.updateContent(content);
        });

        return message;
    }

    public void deleteMessage(UUID id) {
        Message message = validateExistenceMessage(id);
        Channel channel = message.getChannel();
        User user = message.getUser();

        channel.getMessages().removeIf(m -> id.equals(m.getId()));
        user.getMessages().removeIf(m -> id.equals(m.getId()));

        messageRepository.deleteById(id);
    }

    public List<Message> readMessagesByChannel(UUID channelId) {
        Channel channel = channelService.find(channelId);
        return channel.getMessages();
    }

    public List<Message> readMessagesByUser(UUID userId) {
        User user = userService.find(userId);
        return user.getMessages();
    }

    private Message validateExistenceMessage(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메세지 id는 존재하지 않습니다."));
    }

}
