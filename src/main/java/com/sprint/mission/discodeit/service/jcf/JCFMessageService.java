package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.ArrayList;

public class JCFMessageService implements MessageService {
    private final ChannelService channelService;
    private final UserService userService;
    private final Map<UUID, List<Message>> data;

    public JCFMessageService(ChannelService channelService, UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
        this.data = new HashMap<>();
    }

    @Override
    public Message createMessage(UUID channelId, UUID userId, String content) {
        // 존재하는 채널과 유저인지 검색 및 검증
        Channel channel = channelService.findChannelById(channelId);
        User user = userService.findUserById(userId);

        // 채널에 가입된 유저만 메시지를 작성할 수 있다.
        Set<UUID> joinedChannels = userService.getJoinedChannels(userId);
        if (!joinedChannels.contains(channelId)) {
            throw new RuntimeException("채널의 멤버가 아닙니다.");
        }

        // 메시지 생성
        Message message = new Message(channel, user, content);
        // 채널의 메시지 목록이 없으면 생성하고, 해당 채널의 메시지 목록에 메시지를 추가
        data.computeIfAbsent(channelId, id -> new ArrayList<>()).add(message);

        return message;
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        // 메시지를 조회하려는 채널이 실제로 존재하는지 검증
        channelService.findChannelById(channelId);
        // 채널의 메시지 목록 조회, 메시지가 없다면 빈 목록 반환 (null 처리 방지)
        return data.getOrDefault(channelId, Collections.emptyList());
    }

    @Override
    public Message updateMessageContent(UUID channelId, UUID userId, UUID messageId, String newMessage) {
        // findMessage()에서 채널/유저/메시지 검색 및 검증
        Message message = findMessage(channelId, userId, messageId);
        // 메시지 내용 수정
        return message.updateMessageContent(newMessage);
    }

    @Override
    public void deleteMessage(UUID channelId, UUID userId, UUID messageId) {
        // findMessage()에서 채널/유저/메시지 검색 및 검증
        Message message = findMessage(channelId, userId, messageId);
        // 해당 채널의 메시지 목록에서 메시지 제거
        List<Message> messageList = data.getOrDefault(channelId, Collections.emptyList());
        messageList.remove(message);
    }

    private Message findMessage(UUID channelId, UUID userId, UUID messageId) {
        // 메시지 조회 전, 채널과 유저가 실제로 존재하는지 검증
        channelService.findChannelById(channelId);
        userService.findUserById(userId);

        // 채널에 가입된 유저만 조회 가능
        Set<UUID> joinChannels = userService.getJoinedChannels(userId);
        if (!joinChannels.contains(channelId)) {
            throw new RuntimeException("채널의 멤버가 아닙니다.");
        }

        // 해당 채널의 메시지 목록을 조회 (메시지가 없으면 빈 목록 반환)
        List<Message> messageList = data.getOrDefault(channelId, Collections.emptyList());

        // 메시지ID와 작성자가 모두 일치하는 메시지 탐색
        Message message = messageList.stream()
                .filter(m -> m.getId().equals(messageId))
                .filter(m -> m.getUser().getId().equals(userId))
                .findFirst()
                .orElse(null);

        // 메시지가 존재하지 않거나 작성자가 아닐 경우 예외 발생
        if (message == null) {
            throw new RuntimeException("메시지가 존재하지 않거나 권한이 없습니다.");
        }

        return message;
    }
}
