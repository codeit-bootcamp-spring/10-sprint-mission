package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messages = new HashMap<>();

    @Override
    public void sendMessage(User user, Channel channel, String content) {
        Message message = new Message(user, channel, content);
        // 메시지 생성 및 리스트에 추가
        messages.put(message.getId(), message);
    }

    @Override
    public List<Message> getAllMessages() {
        return messages.values().stream().toList();
    }

    // 유저 아이디에 따라 메시지 리스트 반환
    @Override
    public List<Message> getMessageListByUser(UUID userId) {
        return messages.values().stream()
                .filter(id -> id.getSentUserId().equals(userId))
                .toList();
    }

    // 채널 아이디에 따라 메시지 리스트 반환
    @Override
    public List<Message> getMessageListByChannel(UUID channelId) {
        return messages.values().stream()
                .filter(id -> id.getSentChannelId().equals(channelId))
                .toList();
    }

    // 메시지 아이디에 따라 해당 메시지 반환
    @Override
    public Optional<Message> getMessageByMessageId(UUID messageId) {
        return messages.values().stream()
                .filter(id -> id.getId().equals(messageId))
                .findFirst();
    }

    @Override
    public void editMessage(UUID messageId, String newContent) {
        messages.values().stream()
                .filter(id -> id.getId().equals(messageId))
                .findFirst()
                .ifPresent(msg -> msg.updateContent(newContent));
    }

    @Override
    public void deleteMessage(UUID messageId) {
        messages.values().stream()
                .filter(id -> id.getId().equals(messageId))
                .findFirst()
                .ifPresent(msg -> messages.remove(messageId));
    }

    @Override
    public void clearMessage(UUID channelId) {
        messages.values().removeIf(
                message -> message.getSentChannelId().equals(channelId)
        );
    }
}
