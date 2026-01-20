package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

public class BasicMessageService implements MessageService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public BasicMessageService(ChannelRepository channelRepository, UserRepository userRepository, MessageRepository messageRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Message create(String text, UUID channelId, UUID userId) {
        User user = userRepository.findUserById(userId);
        Channel channel = channelRepository.findChannelById(channelId);

        boolean isMember = channel.getUsers().stream()
                .anyMatch(u -> u.getId().equals(userId));

        if (!isMember) {
            throw new IllegalArgumentException("채널에 참여하지 않아 메시지를 보낼 수 없습니다.");
        }

        Message message = new Message(text, user, channel);

        messageRepository.save(message);
        return message;
    }

    @Override
    public List<Message> findMessagesByUserAndChannel(UUID channelId, UUID userId) {
        return messageRepository.findAllMessages().stream()
                .filter(message -> message.getUser().getId().equals(userId))
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    @Override
    public List<Message> findMessagesByChannel(UUID channelId) {
        return messageRepository.findAllMessages().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    @Override
    public List<Message> findMessagesByUser(UUID userId) {
        return messageRepository.findAllMessages().stream()
                .filter(message -> message.getUser().getId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findAllMessages() {
        return messageRepository.findAllMessages();
    }

    @Override
    public Message findMessageById(UUID messageId) {
        return messageRepository.findAllMessages().stream()
                .filter(message -> message.getId().equals(messageId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메세지 아이디입니다."));
    }

    @Override
    public Message update(UUID messageId, String text) {
        Message message = messageRepository.findMessageById(messageId);
        message.update(text);
        messageRepository.save(message);
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findMessageById(messageId);
        messageRepository.delete(message);
    }
}
