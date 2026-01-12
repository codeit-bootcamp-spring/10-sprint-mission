package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageMap = new HashMap<>();
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
        if(!messageMap.containsKey(messageId)) {
            throw new IllegalStateException("해당 ID의 메시지가 존재하지 않음. ID: " + messageId);
        }
        return messageMap.get(messageId);
    }

    // Service Implementation
    // Create
    @Override
    public Message createMessage(String content, UUID userId, UUID channelId) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용을 입력해야 합니다.");
        }

        // 참조 무결성 검사
        // 1. 존재하는 유저인가?
        userService.findUserByUserId(userId);
        // 2. 존재하는 채널인가?
        channelService.findChannelById(channelId);

        Message message = new Message(content, userId, channelId);
        messageMap.put(message.getId(), message);

        return message;
    }

    // Read
    @Override
    public Message findMessageById(UUID messageId) {
        return findMessageByIdOrThrow(messageId);
    }

    @Override
    public List<Message> findAllMessagesByChannelId(UUID channelId) {
        // 채널 존재 여부 확인
        channelService.findChannelById(channelId);

        // 스트림으로 필터링
        return messageMap.values().stream()
                .filter(msg -> msg.getChannelId().equals(channelId)) // 채널 ID로 필터링
                .sorted(Comparator.comparing(Message::getCreatedAt)) // 작성 시간 순 정렬
                .collect(Collectors.toList());
    }

    // Update
    @Override
    public Message updateMessage(UUID messageId, String newContent) {
        Message message = findMessageByIdOrThrow(messageId);

        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("수정할 내용이 비어있습니다.");
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