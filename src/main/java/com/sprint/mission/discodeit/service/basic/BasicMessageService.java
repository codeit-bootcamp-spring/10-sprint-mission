package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
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

    @Override
    public Message create(MessageCreateRequest request) {
        Channel channel = channelRepository
                .findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        User user = userRepository
                .findById(request.authorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Message message = new Message(request.content(), request.channelId(), request.authorId());
        messageRepository.save(message);
        return message;
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
    public List<Message> findallByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findByChannelId(channelId);
        return messages;
    }

    @Override
    public void update(MessageUpdateRequest request) {
        Message message = messageRepository.findById(request.messageId())
                .orElseThrow(() -> new NoSuchElementException("Message not found"));
        message.update(request.content());
        message.setAttachments(request.attachments());
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
