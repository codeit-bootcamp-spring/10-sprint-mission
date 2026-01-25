package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public BasicMessageService(MessageRepository messageRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message createMessage(String content, UUID senderId, UUID channelId) {
        User senderUser = userRepository.findById(senderId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + senderId));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));
        Message message = new Message(content, senderUser, channel);
        messageRepository.save(message);
        userRepository.save(senderUser);
        channelRepository.save(channel);
        System.out.println("메세지 생성이 완료되었습니다.");
        return message;
    }

    @Override
    public Message findId(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found: " + messageId));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> findMessagesById(UUID userId) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getUser().getId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findMessagesByChannel(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    @Override
    public List<Message> findMessagesByUserAndChannel(UUID userId, UUID channelId) {
        return findMessagesById(userId).stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    @Override
    public Message update(UUID messageId, String content) {
        Message foundMsg = findId(messageId);
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("내용이 비어있거나 공백입니다.");
        }
        foundMsg.update(content);
        messageRepository.save(foundMsg);
        return foundMsg;
    }

    @Override
    public void delete(UUID messageId) {
        messageRepository.delete(messageId);
    }

    @Override
    public void deleteAll() {
        messageRepository.deleteAll();
        System.out.println("모든 메시지 데이터를 초기화했습니다.");
    }
}
