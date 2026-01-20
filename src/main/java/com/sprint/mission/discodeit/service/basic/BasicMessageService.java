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
import java.util.Objects;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public BasicMessageService(
            UserRepository userRepository,
            ChannelRepository channelRepository,
            MessageRepository messageRepository
    ) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Message sendMessage(UUID userId, UUID channelId, String content) {
        User user = findUserOrThrow(userId);
        Channel channel = findChannelOrThrow(channelId);
        Message message = messageRepository.save(new Message(user, channel, content));
        // 영속화
        userRepository.save(user);
        channelRepository.save(channel);

        return message;
    }

    @Override
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> getMessageListByUser(UUID userId) {
        findUserOrThrow(userId);

        return messageRepository.findAll().stream()
                .filter(message -> message.getSentUser().getId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> getMessageListByChannel(UUID channelId) {
        findChannelOrThrow(channelId);

        return messageRepository.findAll().stream()
                .filter(message -> message.getSentChannel().getId().equals(channelId))
                .toList();
    }

    @Override
    public Message getMessageByMessageId(UUID messageId) {
        return findMessageOrThrow(messageId);
    }

    @Override
    public Message updateMessage(UUID messageId, String newContent) {
        Message message = findMessageOrThrow(messageId);
        message.updateContent(newContent);
        messageRepository.save(message);
        return message;
    }

    @Override
    public void deleteMessage(UUID messageId) {
        findMessageOrThrow(messageId);
        messageRepository.deleteById(messageId);
    }

    @Override
    public void clearChannelMessage(UUID channelId) {
        findChannelOrThrow(channelId);
        List<UUID> messageIds = messageRepository.findAll().stream()
                .filter(message -> message.getSentChannel().getId().equals(channelId))
                .map(Message::getId)
                .toList();

        if (messageIds.isEmpty()) {
            throw new NoSuchElementException("해당 채널에 보낸 메시지가 없습니다.");
        }

        messageRepository.deleteByChannelId(channelId);
        channelRepository.save(findChannelOrThrow(channelId));
    }

    private Message findMessageOrThrow(UUID messageId) {
        Objects.requireNonNull(messageId, "messageId는 null값일 수 없습니다.");

        return messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 id를 가진 메시지가 존재하지 않습니다."));
    }

    private Channel findChannelOrThrow(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");

        return channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 id를 가진 채널이 존재하지 않습니다."));
    }

    private User findUserOrThrow(UUID userId) {
        Objects.requireNonNull(userId, "userId는 null값일 수 없습니다.");

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 id를 가진 사용자가 존재하지 않습니다."));
    }
}
