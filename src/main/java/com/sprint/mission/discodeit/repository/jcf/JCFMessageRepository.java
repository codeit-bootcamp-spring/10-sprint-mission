package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;


public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID,Message> messages = new LinkedHashMap<>();
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageRepository(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        // 1. 유저와 채널 객체 조회 (비즈니스 검증)
        User sender = userService.findUser(userId);       // 유저가 존재하는지 확인
        Channel channel = channelService.findChannel(channelId); // 채널 존재 확인

        // 2. 메시지 생성 및 Map에 저장
        Message message = new Message(sender, channel, content);
        messages.put(message.getId(), message); // messages는 JCF 기반 Map

        // 3. 생성된 메시지 반환
        return message;
    }

    @Override
    public Message findMessage(UUID messageId) {
        Message message = messages.get(messageId);

        if (message == null) {
            throw new MessageNotFoundException();
        }
        return message;
    }

    @Override
    public List<Message> findAllMessage() {
        return new ArrayList<>(messages.values());
    }
    @Override
    public List<Message> findAllByChannelMessage(UUID channelId) {
        channelService.findChannel(channelId);

        return messages.values().stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .toList();
    }


    @Override
    public void deleteMessage(UUID messageId) {
        if (messages.remove(messageId) == null) {
            throw new MessageNotFoundException();
        }
    }

    @Override
    public Message updateMessage(UUID messageId, String content) {
        Message message = findMessage(messageId);
        message.updateContent(content);

        return message;
    }
}