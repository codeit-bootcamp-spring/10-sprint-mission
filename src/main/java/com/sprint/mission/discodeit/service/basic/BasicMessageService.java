package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

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
    public Message create(UUID channelId, UUID senderId, String text) {
        Channel channel = channelService.read(channelId);
        User sender = userService.read(senderId);

        Message message = new Message(channel, sender, text);
        return messageRepository.save(message);
    }

    @Override
    public Message read(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 ID의 메시지를 찾을 수 없습니다."));
    }

    @Override
    public List<Message> readAll() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> getMessagesByUser(UUID userId) {
        return List.of();
    }

    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        return List.of();
    }

    @Override
    public Message update(UUID id, String text) {
        Message message = read(id);
        message.update(text);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) {
        Message message = read(id);
        messageRepository.delete(id);

    }

    @Override
    public void deleteMessageByUserId(UUID userId) {
        messageRepository.findAll().stream()
                .filter(m -> m.getSender().getId().equals(userId))
                .forEach(m -> messageRepository.delete(m.getId()));
    }

    @Override
    public void deleteMessageByChannelId(UUID channelId) {
        messageRepository.findAll().stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .forEach(m -> messageRepository.delete(m.getId()));
    }
}
