package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messages = new HashMap<>();

    @Override
    public Message sendMessage(User user, Channel channel, String content) {
        Message message = new Message(user, channel, content);
        // 메시지 생성 및 리스트에 추가
        messages.put(message.getId(), message);
        user.updateSentMessages(message);

        return message;
    }

    @Override
    public List<Message> getAllMessages() {
        return messages.values().stream().toList();
    }

    // 유저 아이디에 따라 메시지 리스트 반환
    @Override
    public List<Message> getMessageListByUser(UUID userId) {
        return messages.values().stream()
                .filter(id -> id.getSentUser().getId().equals(userId))
                .toList();
    }

    // 채널 아이디에 따라 메시지 리스트 반환
    @Override
    public List<Message> getMessageListByChannel(UUID channelId) {
        return messages.values().stream()
                .filter(id -> id.getSentChannel().getId().equals(channelId))
                .toList();
    }

    // 메시지 아이디에 따라 해당 메시지 반환
    @Override
    public Optional<Message> getMessageByMessageId(UUID messageId) {
        Objects.requireNonNull(messageId, "messageId는 null일 수 없습니다");

        return Optional.ofNullable(messages.get(messageId));
    }

    @Override
    public Message updateMessage(UUID messageId, String newContent) {
        Message message = checkNoSuchElementException(messageId);

        message.updateContent(newContent);
        return message;
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = messages.remove(messageId);
        checkNoSuchElementException(messageId);

        message.getSentUser().removeSentMessage(message);
    }

    @Override
    public void clearChannelMessage(UUID channelId) {
        List<UUID> messageId = messages.values().stream()
                        .filter(message -> message.getSentChannel().getId().equals(channelId))
                        .map(Message::getId)
                        .toList();

        if (messageId.isEmpty()) {
            throw new NoSuchElementException("해당 채널에 메시지가 없습니다");
        }

        messageId.forEach(messages::remove);
    }

    private Message checkNoSuchElementException(UUID messageId) {
        Objects.requireNonNull(messageId, "messageId는 null일 수 없습니다.");

        Message message = messages.get(messageId);

        if (message == null) {
            throw new NoSuchElementException("해당 id를 가진 메시지가 존재하지 않습니다.");
        }

        return message;
    }
}
