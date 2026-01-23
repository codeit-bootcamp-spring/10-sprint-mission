package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateString;

public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public BasicMessageService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    // 메시지 생성
    @Override
    public Message createMessage(String message, UUID userId, UUID channelId, MessageType type) {
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
        Channel targetChannel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        Message newMessage = new Message(message, sender, targetChannel, type);
        messageRepository.save(newMessage);

        sender.addMessage(newMessage);              // 발행자 메시지 목록에 메시지 추가
        userRepository.save(sender);

        targetChannel.addMessage(newMessage);       // 발행된 채널의 메시지 목록에 메시지 추가
        channelRepository.save(targetChannel);

        return newMessage;
    }

    // 메시지 단건 조회
    @Override
    public Message searchMessage(UUID targetMessageId) {
        return messageRepository.findById(targetMessageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메시지가 존재하지 않습니다."));
    }

    // 메시지 전체 조회
    @Override
    public List<Message> searchMessageAll() {
        return messageRepository.findAll();
    }

    // 특정 유저가 발행한 메시지 다건 조회
    public List<Message> searchMessagesByUserId(UUID targetUserId) {
        userRepository.findById(targetUserId).orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

        List<Message> messages = searchMessageAll();               // 함수가 실행된 시점에서 가장 최신 메시지 목록

        return messages.stream()
                .filter(message -> message.getUser().getId().equals(targetUserId))
                .toList();
    }

    // 특정 채널의 메시지 발행 리스트 조회
    public List<Message> searchMessagesByChannelId(UUID targetChannelId) {
        channelRepository.findById(targetChannelId).orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        List<Message> messages = searchMessageAll();

        return messages.stream()
                .filter(message -> message.getChannel().getId().equals(targetChannelId))
                .toList();
    }

    // 메시지 수정
    @Override
    public Message updateMessage(UUID targetMessageId, String newMessage) {
        Message targetMessage = searchMessage(targetMessageId);

        // 메시지 내용 수정
        Optional.ofNullable(newMessage)
                .ifPresent(message -> {
                    validateString(message, "[메시지 변경 실패] 올바른 메시지 형식이 아닙니다.");
                    validateDuplicateValue(targetMessage.getMessage(), message, "[메시지 변경 실패] 이전 메시지와 동일합니다.");
                    targetMessage.updateMessage(newMessage);
                });

        messageRepository.save(targetMessage);
        return targetMessage;
    }

    @Override
    public void updateMessage(UUID channelId, Channel channel) {}

    // 메시지 삭제
    @Override
    public void deleteMessage(UUID targetMessageId) {
        Message targetMessage = searchMessage(targetMessageId);

        User targetUser = userRepository.findById(targetMessage.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));                     // 사용자 내 메시지 목록 연쇄 삭제
        targetUser.getMessages().removeIf(message -> message.getId().equals(targetMessage.getId()));
        userRepository.save(targetUser);

        Channel targetChannel = channelRepository.findById(targetMessage.getChannel().getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));                      // 채널 내 메시지 목록 연쇄 삭제
        targetChannel.getMessages().removeIf(message -> message.getId().equals(targetMessage.getId()));
        channelRepository.save(targetChannel);

        messageRepository.delete(targetMessage);
    }
}
