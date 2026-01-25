package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID,Message> messages = new LinkedHashMap<>();
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService,  ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }
    public UserService getUserService() {
        return this.userService;
    }

    // 메시지 생성
    public Message createMessage(UUID userId, UUID channelId, String content) {
        // 1. 유저와 채널 객체 조회 (비즈니스 검증)
        User sender = userService.findUser(userId);
        Channel channel = channelService.findChannel(channelId);

        Message message = new Message(sender, channel, content);
        messages.put(message.getId(), message);
        return message;
    }

    // 단건 조회
    public Message findMessage(UUID messageId) {
        Message message = messages.get(messageId);

        if (message == null) {
            throw new MessageNotFoundException();
        }

        return message;
    }

    // 채널별 메시지 조회
    public List<Message> findAllByChannelMessage(UUID channelId) {
        channelService.findChannel(channelId);

        return messages.values().stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .toList();
    }

    // 전체 메시지 조회
    public List<Message> findAllMessage() {
        return new ArrayList<>(messages.values());
    }

    // 메시지 수정
    public Message updateMessage(UUID messageId, String newContent) {
        Message message = findMessage(messageId);
        message.updateContent(newContent);
        return message;
    }

    // 메시지 삭제
    public void deleteMessage(UUID messageId) {
        if (messages.remove(messageId) == null) {
            throw new MessageNotFoundException();
        }
    }

    // 특정 유저 메시지 조회
    public List<Message> findAllByUserMessage(UUID userId) {
        userService.findUser(userId);

        List<Message> userMessages = messages.values().stream()
                .filter(message -> message.getSender().getId().equals(userId))
                .toList();

        if (userMessages.isEmpty()) {
            throw new MessageNotFoundException();
        }

        return userMessages;
    }


}
