package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageMap = new ConcurrentHashMap<>();
    // 의존성 (다른 서비스들)
    // 메시지를 만들 때 유저와 채널의 존재 여부를 확인하기 위해 필요
    private final UserService userService;
    private final ChannelService channelService;

    // 생성자 주입 (Constructor Injection)
    //   JCFMessageService가 작동하려면 UserService와 ChannelService가 반드시 필요함
    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    private Message findMessageByIdOrThrow(UUID messageId) {
        Message message = messageMap.get(messageId);
        if (message == null) {
            throw new IllegalArgumentException("해당 ID의 메시지가 존재하지 않음 ID: " + messageId);
        }
        return message;
    }

    // Service Implementation
    // Create
    @Override
    public Message createMessage(String content, UUID userId, UUID channelId) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용을 입력해야 함");
        }

        // ID로 객체 조회 (참조 무결성 검사 + 객체 확보)
        // 리턴된 User 객체를 받아서 저장
        User sender = userService.findUserByUserId(userId);
        Channel channel = channelService.findChannelById(channelId);

        Message message = new Message(content, sender, channel);
        messageMap.put(message.getId(), message);
        channel.addMessage(message);  // 채널 객체 내부 리스트에도 동기화

        return message;
    }

    // Read
    @Override
    public Message findMessageById(UUID messageId) {
        return findMessageByIdOrThrow(messageId);
    }

    @Override
    public List<Message> findAllMessagesByChannelId(UUID channelId) {
        channelService.findChannelById(channelId);

        // 스트림으로 필터링
        return messageMap.values().stream()
                .filter(msg -> msg.getChannelId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt)) // 작성 시간 순 정렬
                .collect(Collectors.toList());
    }

    // Update
    @Override
    public Message updateMessage(UUID messageId, String newContent) {
        Message message = findMessageByIdOrThrow(messageId);

        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("수정할 내용이 비어있음");
        }
        message.updateContent(newContent);
        return message;
    }

    // Delete
    @Override
    public void deleteMessage(UUID messageId) {
        findMessageByIdOrThrow(messageId);
        messageMap.remove(messageId);
    }
}