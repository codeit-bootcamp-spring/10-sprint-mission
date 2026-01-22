package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public BasicMessageService(
            ChannelRepository channelRepository,
            UserRepository userRepository,
            MessageRepository messageRepository
    ) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Message createMessage(User sender, Channel channel, String content) {
        // 비즈니스 검증
        userRepository.findUser(sender.getId());
        channelRepository.findChannel(channel.getId());

        // 저장은 Repository 책임
        return messageRepository.createMessage(sender, channel, content);
    }

    @Override
    public Message findMessage(UUID messageId) {
        return messageRepository.findMessage(messageId);
    }

    @Override
    public List<Message> findAllByChannelMessage(UUID channelId) {
        channelRepository.findChannel(channelId);
        return messageRepository.findAllByChannelMessage(channelId);
    }

    @Override
    public List<Message> findAllMessage() {
        return messageRepository.findAllMessage();
    }

    @Override
    public List<Message> findAllByUserMessage(UUID userId) {
        // 1. 유저 존재 확인
        userRepository.findUser(userId);

        // 2. 전체 채널 탐색
        List<Message> result = channelRepository.findAllChannel().stream()
                .filter(channel -> channel.hasUserId(userId))  // 채널에 유저 포함 여부
                .flatMap(channel -> messageRepository.findAllByChannelMessage(channel.getId()).stream()) // 채널 메시지 스트림
                .filter(message -> message.getSender().getId().equals(userId)) // 유저가 보낸 메시지
                .toList();

        // 3. 메시지 없으면 예외
        if (result.isEmpty()) throw new MessageNotFoundException();

        return result;
    }

    @Override
    public Message updateMessage(UUID messageId, String newContent) {
        return messageRepository.updateMessage(messageId, newContent);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        messageRepository.deleteMessage(messageId);
    }
}
