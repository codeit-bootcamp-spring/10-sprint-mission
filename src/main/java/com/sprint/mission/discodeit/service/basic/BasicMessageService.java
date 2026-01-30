package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageServiceDTO;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicMessageService(MessageRepository messageRepository, ChannelRepository channelRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Message create(MessageServiceDTO.MessageCreation model) {
        if (!channelRepository.existsById(model.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + model.channelId());
        }
        if (!userRepository.existsById(model.authorId())) {
            throw new NoSuchElementException("Author not found with id " + model.authorId());
        }

        Message message = new Message(model.content(), model.channelId(), model.authorId());
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(MessageServiceDTO.MessageContentUpdate model) {
        Message message = messageRepository.findById(model.messageId())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + model.messageId() + " not found"));
        message.update(model.newContent());
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException("Message with id " + messageId + " not found");
        }
        messageRepository.deleteById(messageId);
    }
}
