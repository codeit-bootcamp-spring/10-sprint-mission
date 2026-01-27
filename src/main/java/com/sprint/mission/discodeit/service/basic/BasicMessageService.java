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
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    @Override
    public Message createMessage(UUID channelId, UUID userId, String content) {
        // 존재하는 유저인지 검색 및 검증
        Channel channel = channelRepository.findChannelById(channelId)
                .orElseThrow(() -> new RuntimeException("채널이 존재하지 않습니다."));

        // 존재하는 유저인지 검색 및 검증
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        // 채널에 가입된 유저만 메시지 작성 가능
        if (!channel.getUsers().contains(user)) {
            throw new RuntimeException("채널에 가입되어 있지 않습니다.");
        }

        // 메시지 생성
        Message message = new Message(channel, user, content);
        // 메시지를 소유해야 하는 채널과 유저의 메시지 목록에 추가
        message.addToChannelAndUser();

        // 메시지 저장 반영
        messageRepository.saveMessage(message);
        userRepository.saveUser(user);
        channelRepository.saveChannel(channel);

        return message;
    }

    @Override
    public List<String> readMessagesByChannelId(UUID channelId) {
        // 메시지를 조회하려는 채널이 존재하는지 검증
        Channel channel = channelRepository.findChannelById(channelId)
                .orElseThrow(() -> new RuntimeException("채널이 존재하지 않습니다."));

        // 해당 채널의 모든 메시지를 반환
        return channel.getMessages()
                .stream()
                .map(Message::formatForDisplay)
                .toList();
    }

    @Override
    public List<String> readMessagesByUserId(UUID userId) {
        // 메시지를 조회하려는 유저가 존재하는지 검증
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        // 해당 유저가 작성한 모든 메시지를 반환
        return user.getMessages()
                .stream()
                .map(Message::formatForDisplay)
                .toList();
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        // 메시지를 조회하려는 채널이 존재하는지 검증
        Channel channel = channelRepository.findChannelById(channelId)
                .orElseThrow(() -> new RuntimeException("채널이 존재하지 않습니다."));

        // 해당 채널의 모든 메시지 정보를 반환
        return channel.getMessages();
    }

    @Override
    public Message findMessageByChannelIdAndMessageId(UUID channelId, UUID messageId) {
        // 존재하는 채널인지 검색 및 검증
        Channel channel = channelRepository.findChannelById(channelId)
                .orElseThrow(() -> new RuntimeException("채널이 존재하지 않습니다."));

        // 메시지가 존재하지 않거나 채널이 일치하지 않을 경우 예외 발생
        return messageRepository.findMessageByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("메시지가 존재하지 않습니다."));
    }

    @Override
    public Message updateMessageContent(UUID channelId, UUID userId, UUID messageId, String newContent) {
        // 메시지 검색 및 권한 확인
        Message message = validateMessageAccess(channelId, userId, messageId);
        // 메시지 내용 수정
        message.updateMessageContent(newContent);

        // 수정 내용 반영
        messageRepository.saveMessage(message);
        userRepository.saveUser(message.getUser());
        channelRepository.saveChannel(message.getChannel());

        return message;
    }

    @Override
    public void deleteMessage(UUID channelId, UUID userId, UUID messageId) {
        // 메시지 검색 및 권한 확인
        Message message = validateMessageAccess(channelId, userId, messageId);
        // 메시지를 소유하고 있는 채널과 유저의 메시지 목록에서 제거
        message.removeFromChannelAndUser();

        // 메시지 삭제 및 삭제 내용 반영
        messageRepository.deleteMessage(message.getId());
        userRepository.saveUser(message.getUser());
        channelRepository.saveChannel(message.getChannel());
    }

    private Message validateMessageAccess(UUID channelId, UUID userId, UUID messageId) {
        // 존재하는 채널인지 검색 및 검증
        Channel channel = channelRepository.findChannelById(channelId)
                .orElseThrow(() -> new RuntimeException("채널이 존재하지 않습니다."));

        // 존재하는 유저인지 검색 및 검증
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        // 채널에 가입된 유저인지 확인
        if (!user.getChannels().contains(channel)) {
            throw new RuntimeException("채널에 가입되어 있지 않습니다.");
        }

        // 메시지가 존재하지 않거나 채널이 일치하지 않을 경우 예외 발생
        Message message = messageRepository.findMessageByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("메시지가 존재하지 않습니다."));
        if (!message.getChannel().equals(channel)) {
            throw new RuntimeException("메시지가 존재하지 않습니다.");
        }

        // 작성자가 아닐 경우 예외 발생
        if (!message.getUser().equals(user)) {
            throw new RuntimeException("해당 메시지에 대한 권한이 없습니다.");
        }

        return message;
    }
}
