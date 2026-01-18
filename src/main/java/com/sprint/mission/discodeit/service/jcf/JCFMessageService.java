package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;
import java.util.stream.Collectors;

// [] 검토 필요
// Service Implementation
public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageMap = new HashMap<>();
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
            throw new IllegalArgumentException("해당 id의 메시지가 존재하지 않습니다. messageId: " + messageId);
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

        boolean isMember = channel.getChannelUserRoles().stream()
                .anyMatch(role -> role.getUser().getId().equals(userId));
        if (!isMember) {
            throw new IllegalArgumentException("해당 채널에 참여하지 않은 유저는 메시지를 보낼 수 없습니다.");
        }

        Message message = new Message(content, sender, channel);
        messageMap.put(message.getId(), message);
        channel.addMessage(message);  // 채널 객체 내부 리스트에도 동기화

        return message;
    }

    // Read
    // 특정 메시지 단건 조회
    @Override
    public Message findMessageById(UUID messageId) {
        return findMessageByIdOrThrow(messageId);
    }
    // 특정 채널의 전체 메시지 조회
    @Override
    public List<Message> findAllMessagesByChannelId(UUID channelId) {
        channelService.findChannelById(channelId);

        // 스트림으로 필터링
        return messageMap.values().stream()
                .filter(msg -> msg.getChannelId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt)) // 작성 시간 순 정렬
                .collect(Collectors.toList());
    }
    // 특정 유저가 작성한 전체 메시지 조회
    // 특정 유저가 특정 채널에서 보낸 메시지 조회

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
    // 특정 메시지 삭제
    public void deleteMessage(UUID messageId) {
        // 1 지울 메시지 객체 확보 (없으면 여기서 예외 발생)
        Message message = findMessageByIdOrThrow(messageId);

        // 2 해당 메시지가 속한 채널(방) 객체를 찾아가서, 거기 리스트에서도 삭제
        // Message 객체가 channelId를 가지고 있으므로 이를 이용해 채널을 찾음
        Channel channel = channelService.findChannelById(message.getChannelId());
        channel.removeMessage(message); // Channel 엔티티의 메서드 호출

        // 3. 이제 서비스 맵에서 영구 삭제
        messageMap.remove(messageId);
        System.out.println("메시지 삭제 완료: " + message.getContent());
    }
    // 특정 유저가 작성한 모든 메시지 삭제 - 유저 삭제 -> 유저가 작성한 모든 메시지 삭제
    @Override
    public void deleteAllMessagesByUserId(UUID userId) { // (4)
        // 1 이 유저가 쓴 모든 메시지를 찾음
        List<Message> userMessages = messageMap.values().stream()
                .filter(message -> message.getSenderId().equals(userId))
                .toList();

        // 2 찾은 메시지들을 하나씩 순회하며 채널 명부에서 지움
        for (Message msg : userMessages) {
            try {
                Channel channel = channelService.findChannelById(msg.getChannelId());
                channel.removeMessage(msg);
            } catch (IllegalArgumentException e) {
                // 이미 채널이 삭제된 경우 등 예외가 발생하면 무시하고 진행 (수정 필요)
            }
        }

        // 3 Map(저장소)에서 일괄 삭제
        messageMap.values().removeAll(userMessages);
        System.out.println("[5] 해당 유저가 작성한 모든 메시지를 삭제했습니다." +
                "\n\tusername: " + userService.findUserByUserId(userId).getUsername() );
    }
    // 특정 채널 내 모든 메시지 삭제 - 유저가 OWNER인 채널 삭제 -> 해당 채널 내 모든 메시지 삭제
    @Override
    public void deleteAllMessagesByChannelId(UUID channelId) { // (3-1-1)
        // 메시지 맵에서 해당 채널 ID를 가진 메시지 일괄 삭제
        messageMap.values().removeIf(msg -> msg.getChannelId().equals(channelId));
        System.out.println("\t\t[1] 채널 내 모든 메시지 삭제 완료. (채널 삭제 사전 작업 1)" +
                "\n\t\t\t channelId: " + channelId
        );
    }
}