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
        return messages.entrySet().stream()
                .filter(message -> message.getKey().equals(messageId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 id를 가진 메시지가 존재하지 않습니다."))
                .getValue();
    }

    @Override
    public void editMessage(UUID messageId, String newContent) {
        messages.values().stream()
                .filter(id -> id.getId().equals(messageId))
                .findFirst()
                .ifPresent(msg -> {
                    try {
                        msg.updateContent(newContent);
                    } catch (IllegalArgumentException e) {
                        System.out.println("해당 id를 가진 메시지가 존재하지 않습니다.");
                    }
                });
    }

    @Override
    public void deleteMessage(UUID messageId) {
        messages.values().stream()
                .filter(id -> id.getId().equals(messageId))
                .findFirst()
                .ifPresent(msg ->
                        {
                            try {
                                messages.remove(messageId);
                            } catch (IllegalArgumentException e) {
                                System.out.println("해당 id를 가진 메시지가 존재하지 않습니다.");
                            }
                        });
    }

    @Override
    public void clearMessage(UUID channelId) {
        try {
            messages.values().removeIf(
                    message -> message.getSentChannelId().equals(channelId)
            );
        } catch (IllegalArgumentException e) {
            System.out.println("해당 id를 가진 채널이 존재하지 않습니다.");
        }
    }
}
