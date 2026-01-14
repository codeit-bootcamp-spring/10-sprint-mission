package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.util.ValidationUtil;

import javax.xml.validation.Validator;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.Main.*;

public class JCFMessageService implements MessageService {
    public static final ArrayList<Message> messages = new ArrayList<>();      // 한 채널에서 발생한 메시지 리스트
    private final ValidationUtil validationUtil = new ValidationUtil();
    private final JCFUserService userService = new JCFUserService();
    private final ChannelService channelService = new JCFChannelService();


    // 메시지 생성
    @Override
    public Message createMessage(String message, User user, Channel channel, MessageType type) {
        User sender = userService.searchUser(user.getId());
        Channel targetChannel = channelService.searchChannel(channel.getId());

        Message newMessage = new Message(message, sender, targetChannel, type);
        messages.add(newMessage);
        return newMessage;
    }

    // 메시지 단건 조회
    @Override
    public Message searchMessage(UUID targetMessageId) {
        return messages.stream()
                        .filter(message -> message.getId().equals(targetMessageId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("해당 메시지가 존재하지 않습니다."));
    }

    // 메시지 전체 조회
    @Override
    public ArrayList<Message> searchMessageAll() {
        return messages;
    }

    // 특정 유저가 발행한 메시지 다건 조회

    // 특정 채널의 메시지 발행 리스트 조회

    // 메시지 수정
    @Override
    public void updateMessage(UUID targetMessageId, String newMessage) {
        Message targetMessage = searchMessage(targetMessageId);

        Optional.ofNullable(newMessage)
                .ifPresent(message -> {
                    validationUtil.validateString(message, "[메시지 변경 실패] 올바른 메시지 형식이 아닙니다.");
                    validationUtil.validateDuplicateValue(targetMessage.getMessage(), message, "[메시지 변경 실패] 이전 메시지와 동일합니다.");
                });

        targetMessage.updateMessage(newMessage);
    }

    // 메시지 삭제
    @Override
    public void deleteMessage(UUID targetMessageId) {
        Message targetMessage = searchMessage(targetMessageId);

        messages.remove(targetMessage);
    }
}
