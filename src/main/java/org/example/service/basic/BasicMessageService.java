package org.example.service.basic;

import org.example.entity.Channel;
import org.example.entity.Message;
import org.example.entity.User;
import org.example.exception.InvalidRequestException;
import org.example.exception.NotFoundException;
import org.example.repository.ChannelRepository;
import org.example.repository.MessageRepository;
import org.example.repository.UserRepository;
import org.example.service.MessageService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;        //  변경
    private final ChannelRepository channelRepository;  //  변경

    public BasicMessageService(MessageRepository messageRepository,
                               UserRepository userRepository,
                               ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message create(String content, UUID senderId, UUID channelId) {
        if (content == null || content.isBlank()) {
            throw new InvalidRequestException("content", "null이 아니고 빈 값이 아님", content);
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("id", "존재하는 유저", senderId));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("id", "존재하는 채널", channelId));

        Message message = new Message(content, sender, channel);
        message.addToChannelAndUser();

        messageRepository.save(message);
        userRepository.save(sender);      //  추가
        channelRepository.save(channel);  //  추가

        return message;
    }

    @Override
    public Message findById(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("id", "존재하는 메시지", messageId));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> findByChannel(UUID channelId) {
        channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("id", "존재하는 채널", channelId));

        return messageRepository.findAll().stream()
                .filter(message -> !message.isDeletedAt())
                .filter(message -> message.getChannel().getId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findBySender(UUID senderId) {
        userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("id", "존재하는 유저", senderId));

        return messageRepository.findAll().stream()
                .filter(message -> !message.isDeletedAt())
                .filter(message -> message.getSender().getId().equals(senderId))
                .collect(Collectors.toList());
    }

    @Override
    public Message update(UUID messageId, String content) {
        Message message = findById(messageId);
        message.updateContent(content);
        message.updateEditedAt(true);
        return messageRepository.save(message);
    }

    @Override
    public void softDelete(UUID messageId) {
        Message message = findById(messageId);
        message.updateDeletedAt(true);
        messageRepository.save(message);
    }

    @Override
    public void hardDelete(UUID messageId) {
        Message message = findById(messageId);
        User sender = message.getSender();
        Channel channel = message.getChannel();

        message.removeFromChannelAndUser();
        messageRepository.deleteById(messageId);
        userRepository.save(sender);      // 추가
        channelRepository.save(channel);  // 추가

    }
}
