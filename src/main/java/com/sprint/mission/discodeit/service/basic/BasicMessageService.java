package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.List;
import java.util.UUID;

public class BasicMessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public BasicMessageService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    public Message createMessage(UUID requestId, UUID channelId, String content) {
        User requester = findUserByUserID(requestId);
        Channel channel = findChannelByChannelId(channelId);

        Message message = new Message(requester, channel, content);

        requester.addMessage(message);
        userRepository.save(requester);

        channel.addMessage(message);
        channelRepository.save(channel);

        messageRepository.save(message);
        return message;
    }

    public Message findMessageByMessageId(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메세지를 찾을 수 없습니다 messageId: " + id));
    }

    public List<Message> findAllMessages() {
        return messageRepository.findAll();
    }

    public Message updateMessage(UUID requestId, UUID messageId, String content) {
        User requester = findUserByUserID(requestId);
        Message message = findMessageByMessageId(messageId);

        message.validateOwner(requester);
        message.updateContent(content);

        messageRepository.save(message);

        User sender = findUserByUserID(message.getSender().getId());
        Channel channel = findChannelByChannelId(message.getChannel().getId());

        sender.getMessages().removeIf(m -> m.getId().equals(messageId));
        sender.getMessages().add(message);

        channel.getMessages().removeIf(m -> m.getId().equals(messageId));
        channel.getMessages().add(message);

        userRepository.save(sender);
        channelRepository.save(channel);

        return message;
    }

    public void deleteMessage(UUID requestId, UUID messageId) {
        User requester = findUserByUserID(requestId);
        Message message = findMessageByMessageId(messageId);

        message.validateOwner(requester);

        message.clear();

        channelRepository.save(message.getChannel());
        userRepository.save(message.getSender());

        messageRepository.delete(message);
    }

    private User findUserByUserID(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다 userId: " + userId));
    }

    private Channel findChannelByChannelId(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다 channelId: " + id));
    }
}
