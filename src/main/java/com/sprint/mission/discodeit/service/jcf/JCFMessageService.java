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
    public Message getMessageByMessageId(UUID messageId) {
        Objects.requireNonNull(messageId, "messageId는 null일 수 없습니다");

        Message message = messages.get(messageId);
        if (message == null) {
            throw new NoSuchElementException("해당 id를 가진 메시지가 존재하지 않습니다.");
        }
        return message;
    }

    @Override
    public void editMessage(UUID messageId, String newContent) {
        Objects.requireNonNull(messageId, "messageId는 null일 수 없습니다.");

        Message message = messages.get(messageId);
        if (message == null) {
            throw new NoSuchElementException("해당 id를 가진 메시지가 존재하지 않습니다.");
        }

        message.updateContent(newContent);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Objects.requireNonNull(messageId, "messageId는 null일 수 없습니다");
        messages.remove(messageId);
    }

    @Override
    public void clearMessage(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");

        messages.values().removeIf(
                message -> channelId.equals(message.getSentChannelId())
        );
    }
}
