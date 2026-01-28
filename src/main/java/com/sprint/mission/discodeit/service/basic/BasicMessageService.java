package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public BasicMessageService(MessageRepository messageRepository, UserRepository userRepository,ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message createMessage(String content, UUID senderId, UUID channelId) {
        Message message = new Message(content, senderId, channelId);
        User sender = userRepository.findById(senderId)
                        .orElseThrow(() -> new NoSuchElementException("발신자를 찾을 수 없습니다."));
        Channel findChannel = channelRepository.findById(channelId)
                        .orElseThrow(() -> new NoSuchElementException("채널을 찾을 수 없습니다."));

        sender.addMessageId(message.getId());
        findChannel.addMessageId(message.getId());

        userRepository.save(sender);
        channelRepository.save(findChannel);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Message getMessage(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 메세지가 존재하지 않습니다."));
    }

    @Override
    public List<Message> getAllMessages() {
        return List.copyOf(messageRepository.findAll());
    }

    @Override
    public List<Message> getMessagesByUserId(UUID userId) {
        return List.copyOf(messageRepository.findAllByUserId(userId));
    }

    @Override
    public List<Message> getMessagesByChannelId(UUID channelId) {
        return List.copyOf(messageRepository.findAllByChannelId(channelId));
    }

    @Override
    public Message updateMessage(UUID messageId, String content) {
        Message findMessage = getMessage(messageId);
        Optional.ofNullable(content)
                .ifPresent(findMessage::updateContent);
        messageRepository.save(findMessage);
        return findMessage;
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = getMessage(messageId);
        User findUser = userRepository.findById(message.getSenderId())
                .orElseThrow(() -> new NoSuchElementException("메세지의 발신자가 존재하지 않습니다."));
        Channel findChannel = channelRepository.findById(message.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("메세지의 채널이 존재하지 않습니다."));
        findUser.removeMessageId(messageId);
        findChannel.removeMessageId(messageId);

        userRepository.save(findUser);
        channelRepository.save(findChannel);
        messageRepository.deleteById(messageId);
    }
}
