/* package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> data;                     // 한 채널에서

    private final UserService jcfUserService;
    private final ChannelService jcfChannelService;

    public JCFMessageService(JCFUserService jcfUserService, JCFChannelService jcfChannelService) {
        this.data = new ArrayList<>();

        this.jcfUserService = jcfUserService;
        this.jcfChannelService = jcfChannelService;
    }

    // 메시지 생성
    @Override
    public Message createMessage(String message, UUID userId, UUID channelId, MessageType type) {
        User sender = jcfUserService.searchUser(userId);
        Channel targetChannel = jcfChannelService.searchChannel(channelId);

        Message newMessage = new Message(message, sender.getId(), targetChannel.getId(), type);
        data.add(newMessage);

        return newMessage;
    }

    // 메시지 단건 조회
    @Override
    public Message searchMessage(UUID targetMessageId) {
        return data.stream()
                .filter(message -> message.getId().equals(targetMessageId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 메시지가 존재하지 않습니다."));
    }

    // 메시지 전체 조회
    @Override
    public List<Message> searchMessageAll() {
        return data;
    }

    // 특정 유저가 발행한 메시지 다건 조회
    public List<Message> searchMessagesByUserId(UUID targetUserId) {
        User targetUser = jcfUserService.searchUser(targetUserId);

        return searchMessageAll().stream()
                .filter(message -> message.getAuthorId().equals(targetUser.getId()))
                .toList();
    }

    // 특정 채널의 메시지 발행 리스트 조회
    public List<Message> searchMessagesByChannelId(UUID targetChannelId) {
        Channel targetChannel = jcfChannelService.searchChannel(targetChannelId);

        return searchMessageAll().stream()
                .filter(message -> message.getChannelId().equals(targetChannelId))
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

        return targetMessage;
    }

    @Override
    public void updateMessage(UUID channelId, Channel channel) {}

    // 메시지 삭제
    @Override
    public void deleteMessage(UUID targetMessageId) {
        Message targetMessage = searchMessage(targetMessageId);

        data.remove(targetMessage);
    }
}
 */