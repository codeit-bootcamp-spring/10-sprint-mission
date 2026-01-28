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

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    // 메시지 생성
    @Override
    public Message create(String content, UUID userId, UUID channelId) {
        validateAccess(userId, channelId); // 권한 확인

        User author = findUserOrThrow(userId);
        Channel channel = findChannelOrThrow(channelId);

        Message newMessage = new Message(content,author, channel);

        author.addMessage(newMessage);
        channel.addMessage(newMessage);

        userRepository.save(author);
        channelRepository.save(channel);
        return messageRepository.save(newMessage);
    }

    // 메시지 ID로 조회
    @Override
    public Message findById(UUID id){
        return messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 메시지 ID입니다."));
    }

    // 메시지 전부 조회
    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    // 메시지 수정
    @Override
    public Message update(UUID id, String content) {
        Message message = findById(id);
        message.update(content); // 여기서 isEdited가 true로 변함

        messageRepository.save(message);
        userRepository.save(message.getUser());
        channelRepository.save(message.getChannel());

        return message;
    }

    // 메시지 삭제
    @Override
    public void delete(UUID id) {
        Message message = findById(id);

        User author = message.getUser();
        Channel channel = message.getChannel();

        author.removeMessage(message);
        channel.removeMessage(message);

        userRepository.save(author);
        channelRepository.save(channel);
        messageRepository.delete(message);
    }

    // 메시지 고정
    @Override
    public Message togglePin(UUID id){
        Message message = findById(id);
        message.togglePin();

        messageRepository.save(message);
        channelRepository.save(message.getChannel());
        userRepository.save(message.getUser());

        return message;
    }

    // 특정 채널의 메시지 목록 조회
    @Override
    public List<Message> findAllByChannelId(UUID channelId, UUID userId) { // 특정 채널의 메시지 조회
        validateAccess(userId, channelId);
        Channel channel = findChannelOrThrow(channelId);
        return channel.getMessages();
    }

    // 권한 확인
    private void validateAccess(UUID userId, UUID channelId) {
        // 채널 멤버 확인
        User user = findUserOrThrow(userId);
        Channel channel = findChannelOrThrow(channelId);
        if (!channel.isMember(user)) {
            throw new IllegalArgumentException("채널 멤버만 접근할 수 있습니다.");
        }
    }

    // 헬퍼 메서드 - 저장소에서 유저 조회
    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저 ID입니다."));
    }

    // 헬퍼 메서드 - 저장소에서 채널 조회
    private Channel findChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널 ID입니다."));
    }
}
