package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public BasicMessageService(MessageRepository messageRepository,
                               UserRepository userRepository,
                               ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message create(UUID userId, UUID channelId, String content) {
        User author = userRepository.findById(userId);
        Channel channel = channelRepository.findById(channelId);

        if (author == null || channel == null) throw new IllegalArgumentException();

        Message newMessage = new Message(author, channel, content);
        messageRepository.save(newMessage);
        return newMessage;
    }

    @Override
    public Message findById(UUID messageId) {
        return messageRepository.findById(messageId);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID messageId, String content) {
        Message message = messageRepository.findById(messageId);
        if (message != null) {
            message.updateContent(content);
            messageRepository.update(message);
            return message;
        }
        return null;
    }

    @Override
    public void delete(UUID messageId) {
        messageRepository.delete(messageId);
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .toList();
    }
}