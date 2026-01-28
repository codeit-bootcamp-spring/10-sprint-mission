package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    public Message create(String content, UUID channelId, UUID userId) {
        Channel channel = channelRepository.findChannelById(channelId);

        Message message = new Message(content, channelId, userId);

        boolean isMember = channel.getUserIds().stream()
                .anyMatch(u -> u.equals(userId));

        if (!isMember) {
            throw new IllegalArgumentException("채널에 참여하지 않아 메시지를 보낼 수 없습니다.");
        }

        messageRepository.save(message);
        return message;
    }

    @Override
    public List<Message> findMessagesByUserAndChannel(UUID channelId, UUID userId) {
        return messageRepository.findAllMessages().stream()
                .filter(message -> message.getAuthorId().equals(userId))
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<Message> findMessagesByChannel(UUID channelId) {
        return messageRepository.findAllMessages().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<Message> findMessagesByUser(UUID userId) {
        return messageRepository.findAllMessages().stream()
                .filter(message -> message.getAuthorId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findAllMessages() {
        return messageRepository.findAllMessages();
    }

    @Override
    public Message findMessageById(UUID messageId) {
        return messageRepository.findMessageById(messageId);
    }

    @Override
    public Message update(UUID messageId, String content) {
        Message message = messageRepository.findMessageById(messageId);
        message.update(content);
        messageRepository.save(message);
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findMessageById(messageId);
        messageRepository.delete(message);
    }
}
