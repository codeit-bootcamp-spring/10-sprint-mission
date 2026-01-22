package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final JCFMessageRepository jcfMessageRepository;
    private final JCFUserService jcfUserService;
    private final JCFChannelService jcfChannelService;

    public JCFMessageService(JCFMessageRepository jcfMessageRepository, JCFUserService jcfUserService, JCFChannelService jcfChannelService) {
        this.jcfMessageRepository = jcfMessageRepository;
        this.jcfUserService = jcfUserService;
        this.jcfChannelService = jcfChannelService;
    }

    // 메시지 생성
    @Override
    public Message createMessage(String message, UUID userId, UUID channelId, MessageType type) {
        User sender = jcfUserService.searchUser(userId);
        Channel targetChannel = jcfChannelService.searchChannel(channelId);

        Message newMessage = new Message(message, sender, targetChannel, type);
        jcfMessageRepository.save(newMessage);

        sender.addMessage(newMessage);              // 발행자 메시지 목록에 메시지 추가
        targetChannel.addMessage(newMessage);       // 발행된 채널의 메시지 목록에 메시지 추가

        return newMessage;
    }

    // 메시지 단건 조회
    @Override
    public Message searchMessage(UUID targetMessageId) {
        return jcfMessageRepository.findById(targetMessageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메시지가 존재하지 않습니다."));
    }

    // 메시지 전체 조회
    @Override
    public List<Message> searchMessageAll() {
        return jcfMessageRepository.findAll();
    }

    // 특정 유저가 발행한 메시지 다건 조회
    public List<Message> searchMessagesByUserId(UUID targetUserId) {
        User targetUser = jcfUserService.searchUser(targetUserId);

        return targetUser.getMessages();
    }

    // 특정 채널의 메시지 발행 리스트 조회
    public List<Message> searchMessagesByChannelId(UUID targetChannelId) {
        Channel targetChannel = jcfChannelService.searchChannel(targetChannelId);

        return targetChannel.getMessages();
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

        return targetMessage;
    }

    // 메시지 삭제
    @Override
    public void deleteMessage(UUID targetMessageId) {
        Message targetMessage = searchMessage(targetMessageId);

        targetMessage.getUser().removeMessage(targetMessage);
        targetMessage.getChannel().removeMessage(targetMessage);
        jcfMessageRepository.delete(targetMessage);
    }
}
