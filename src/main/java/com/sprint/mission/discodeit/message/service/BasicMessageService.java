package com.sprint.mission.discodeit.message.service;

import com.sprint.mission.discodeit.message.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.message.dto.MessageResponse;
import com.sprint.mission.discodeit.message.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.mapper.MessageMapper;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;

    @Override
    public MessageResponse create(MessageCreateRequest request) {
        Channel channel = channelRepository
                .findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        User user = userRepository
                .findById(request.authorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Message message = new Message(request.content(), request.channelId(), request.authorId());
        Message savedMessage = messageRepository.save(message);
        return messageMapper.convertToResponse(savedMessage);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public Optional<Message> findByChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .max(Comparator.comparing(Message::getUpdatedAt));
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findByChannelId(channelId);
        return messages.stream()
                .map(messageMapper::convertToResponse).toList();
    }

    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        Message message = messageRepository.findById(request.messageId())
                .orElseThrow(() -> new NoSuchElementException("Message not found"));
        message.update(request.content());
         message.setAttachments(request.attachments());
        messageRepository.save(message);
        return messageMapper.convertToResponse(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found"));

        if (message.getAttachments() != null) {
            message.getAttachments().forEach(attachment -> {
                binaryContentRepository.deleteById(attachment.binaryContentId());
            });
        }
        messageRepository.deleteById(messageId);
    }
}
