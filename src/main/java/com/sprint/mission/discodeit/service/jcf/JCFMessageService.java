package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// [] 검토 필요
// Service Implementation
public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageMap = new ConcurrentHashMap<>();
    // 의존성 (다른 서비스들)
    // 메시지를 만들 때 유저와 채널의 존재 여부를 확인하기 위해 필요
    private final UserService userService;
    private final ChannelService channelService;

    // 생성자 주입
    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    // id로 Channel 객체 조회 메서드 - 해당 id의 Channel 있으면 Channel 객체 반환. 없으면 예외 발생
    private Message findMessageByIdOrThrow(UUID messageId) {
        Message message = messageMap.get(messageId);
        if (message == null) {
            throw new IllegalArgumentException("해당 ID의 메시지가 존재하지 않습니다. id: " + messageId);
        }
        return message;
    }

    // Create
    @Override
    public Message createMessage(String content, UUID userId, UUID channelId) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용을 입력해야 함");
        }

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

    @Override
    public void deleteAllMessagesByUserId(UUID userId) {
        // 메시지 Map의 값들 중 전송자(Sender)의 ID가 userId와 같은 것을 모두 삭제
        messageMap.values().removeIf(message -> message.getSenderId().equals(userId));
        System.out.println("해당 유저가 작성한 모든 메시지를 삭제했습니다. UserId: " + userId);
    }
}