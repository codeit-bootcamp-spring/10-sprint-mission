package com.sprint.mission.service.jcf;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.entity.Message;
import com.sprint.mission.entity.User;
import com.sprint.mission.service.ChannelService;
import com.sprint.mission.service.MessageService;
import com.sprint.mission.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
    private final UserService userService;
    private final ChannelService channelService;
    private final Map<UUID, Message> messages;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.messages = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(UUID userId, UUID channelId, String content) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        Message message = new Message(user, channel, content);

        messages.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findById(UUID messageId) {
        validateMessageExists(messageId);
        return messages.get(messageId);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return messages.values().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return messages.values().stream()
                .filter(message ->
                        message.getUser().getId().equals(userId) && message.getChannel().getId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public Message update(UUID userId, UUID messageId, String content) {
        Message message = findById(messageId);
        validateAuthentication(message, userId);
        message.updateContent(content);
        return message;
    }

    @Override
    public void deleteById(UUID userId, UUID messageId) {
        validateMessageExists(messageId);
        validateAuthentication(findById(messageId), userId);
        messages.remove(messageId);
    }

    private void validateMessageExists(UUID id) {
        if (!messages.containsKey(id)) {
            throw new IllegalArgumentException("메시지가 존재하지 않습니다.");
        }
    }

    private void validateAuthentication(Message message, UUID targetId) {
        if (!message.getUser().getId().equals(targetId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }
}
