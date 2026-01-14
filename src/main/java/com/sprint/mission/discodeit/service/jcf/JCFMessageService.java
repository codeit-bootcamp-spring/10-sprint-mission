package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final ChannelService channelService;
    private final UserService userService;
    private final List<Message> data;

    public JCFMessageService(ChannelService channelService, UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
        this.data = new ArrayList<>();
    }

    @Override
    public Message createMessage(UUID channelId, UUID userId, String content) {
        // 존재하는 채널과 유저인지 검색 및 검증
        Channel channel = channelService.findChannelById(channelId);
        User user = userService.findUserById(userId);

        // 채널에 가입된 유저만 메시지를 작성할 수 있다.
        List<Channel> joinedChannels = userService.getJoinedChannels(userId);
        if (!joinedChannels.contains(channel)) {
            throw new RuntimeException("채널에 가입되어 있지 않습니다.");
        }

        // 메시지 생성
        Message message = new Message(channel, user, content);
        data.add(message);

        return message;
    }

    @Override
    public List<String> readMessagesByChannelId(UUID channelId) {
        // 메시지를 조회하려는 채널이 실제로 존재하는지 검증
        channelService.findChannelById(channelId);

        // 해당 채널의 모든 메시지를 조회해서 반환, 메시지가 존재하지 않는다면 빈 리스트 반환
        // 메시지의 작성자와 내용만 반환
        return data.stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .map(m -> m.getUser().getNickname() + ": " + m.getContent())
                .toList();
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        // 메시지를 조회하려는 채널이 실제로 존재하는지 검증
        channelService.findChannelById(channelId);

        // 해당 채널의 모든 메시지를 조회해서 반환, 메시지가 존재하지 않는다면 빈 리스트 반환
        return data.stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .toList();
    }

    @Override
    public Message findMessageByChannelIdAndMessageId(UUID channelId, UUID messageId) {
        // 메시지를 조회하려는 채널이 실제로 존재하는지 검증
        channelService.findChannelById(channelId);

        // 메시지가 존재하지 않을 경우 예외 발생
        Message message = data.stream()
                .filter(m -> m.getId().equals(messageId))
                .findFirst()
                .orElse(null);
        if (message == null) {
            throw new RuntimeException("메시지가 존재하지 않습니다.");
        }

        return message;
    }

    @Override
    public Message updateMessageContent(UUID channelId, UUID userId, UUID messageId, String newMessage) {
        // 메시지 검색 및 권한 확인
        Message message = validateMessageAccess(channelId, userId, messageId);
        // 메시지 내용 수정
        return message.updateMessageContent(newMessage);
    }

    @Override
    public void deleteMessage(UUID channelId, UUID userId, UUID messageId) {
        // 메시지 검색 및 권한 확인
        Message message = validateMessageAccess(channelId, userId, messageId);
        // 메시지 삭제
        data.remove(message);
    }

    private Message validateMessageAccess(UUID channelId, UUID userId, UUID messageId) {
        // 메시지 조회 전, 채널이 실제로 존재하는지 검색 및 검증
        Channel channel = channelService.findChannelById(channelId);

        // getJoinedChannels() 내부에서 유저가 존재하는지 검증 후 유저가 가입한 채널 목록 조회
        // 유저가 가입한 채널 중 해당 채널이 존재하는지 확인
        List<Channel> joinedChannels = userService.getJoinedChannels(userId);
        if (!joinedChannels.contains(channel)) {
            throw new RuntimeException("채널에 가입되어 있지 않습니다.");
        }

        // 메시지가 존재하지 않을 경우 예외 발생
        Message message = data.stream()
                .filter(m -> m.getId().equals(messageId))
                .findFirst()
                .orElse(null);
        if (message == null) {
            throw new RuntimeException("메시지가 존재하지 않습니다.");
        }

        // 작성자가 아닐 경우 예외 발생
        UUID writerId = message.getUser().getId();
        if (!writerId.equals(userId)) {
            throw new RuntimeException("해당 메시지에 대한 권한이 없습니다.");
        }

        return message;
    }
}
