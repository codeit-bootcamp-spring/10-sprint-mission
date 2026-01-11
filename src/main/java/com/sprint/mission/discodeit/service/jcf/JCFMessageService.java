package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

    // 1. Memory DB
    private final Map<UUID, Message> messageMap = new HashMap<>();

    // 2. 의존성 (다른 서비스들)
    // 메시지를 만들 때 유저와 채널의 존재 여부를 확인하기 위해 필요합니다.
    private final UserService userService;
    private final ChannelService channelService;

    // 3. 생성자 주입 (Constructor Injection)
    // "JCFMessageService가 작동하려면 UserService와 ChannelService가 반드시 필요해!"라고 선언하는 것
    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(String content, UUID userId, UUID channelId) {
        // [유효성 검사] 내용 없음
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용을 입력해야 합니다.");
        }

        // [참조 무결성 검사] (Foreign Key Constraint Simulation)
        // 1. 존재하는 유저인가?
        userService.findUserByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 존재하는 채널인가?
        channelService.findUserByUserId(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        // [생성 및 저장]
        // 검증을 모두 통과했으므로 안전하게 메시지를 생성합니다.
        Message message = new Message(content, userId, findUserByUserId(userId));
        messageMap.put(message.getId(), message);

        return message;
    }

    @Override
    public Optional<Message> findOne(UUID id) {
        return Optional.ofNullable(messageMap.get(id));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        // [Stream API 활용]
        // SQL: SELECT * FROM message WHERE channel_id = ? ORDER BY created_at ASC
        return messageMap.values().stream()
                .filter(msg -> msg.getChannelId().equals(channelId)) // 1. 해당 채널 메시지만 필터링
                .sorted(Comparator.comparing(Message::getCreatedAt)) // 2. 작성 시간 순으로 정렬 (과거 -> 현재)
                .collect(Collectors.toList());                       // 3. 리스트로 변환
    }

    @Override
    public Message updateMessage(UUID id, String newContent) {
        // 1. 대상 조회
        Message message = findOne(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 메시지를 찾을 수 없습니다."));

        // 2. 유효성 검사
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("수정할 내용이 비어있습니다.");
        }

        // 3. 수정 (Entity 메소드 활용)
        message.updateContent(newContent);

        return message;
    }

    @Override
    public void deleteMessage(UUID id) {
        if (!messageMap.containsKey(id)) {
            throw new IllegalArgumentException("삭제할 메시지가 존재하지 않습니다.");
        }
        messageMap.remove(id);
    }
}