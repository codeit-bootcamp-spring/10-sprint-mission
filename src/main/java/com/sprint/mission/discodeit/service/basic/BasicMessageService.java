package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        // 존재 여부만 검증
        userService.findUser(userId);
        channelService.findChannel(channelId);

        return messageRepository.createMessage(userId, channelId, content);
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