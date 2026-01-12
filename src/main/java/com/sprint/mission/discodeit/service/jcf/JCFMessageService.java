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
        Channel channel = channelService.findChannelById(channelId);
        User user = userService.findUserById(userId);

        Set<UUID> joinedChannels = userService.getJoinedChannels(userId);
        if (!joinedChannels.contains(channelId)) {
            throw new RuntimeException("채널의 멤버가 아닙니다.");
        }

        Message message = new Message(channel, user, content);
        data.computeIfAbsent(channelId, id -> new ArrayList<>()).add(message);

        return message;
    }

    @Override
    public List<Message> readMessages(UUID channelId) {
        channelService.findChannelById(channelId);
        return data.getOrDefault(channelId, Collections.emptyList());
    }

    @Override
    public Message updateMessageContent(UUID channelId, UUID userId, UUID messageId, String newMessage) {
        Message message = findMessage(channelId, userId, messageId);
        message.updateMessageContent(newMessage);
        return message;
    }

    @Override
    public void deleteMessage(UUID channelId, UUID userId, UUID messageId) {
        Message message = findMessage(channelId, userId, messageId);
        List<Message> messageList = data.getOrDefault(channelId, Collections.emptyList());
        messageList.remove(message);
    }

    private Message findMessage(UUID channelId, UUID userId, UUID messageId) {
        channelService.findChannelById(channelId);
        userService.findUserById(userId);

        Set<UUID> joinChannels = userService.getJoinedChannels(userId);
        if (!joinChannels.contains(channelId)) {
            throw new RuntimeException("채널의 멤버가 아닙니다.");
        }

        List<Message> messageList = data.getOrDefault(channelId, Collections.emptyList());

        Message message = messageList.stream()
                .filter(m -> m.getId().equals(messageId))
                .filter(m -> m.getUser().getId().equals(userId))
                .findFirst()
                .orElse(null);

        if (message == null) {
            throw new RuntimeException("메세지가 존재하지 않거나 권한이 없습니다.");
        }

        return message;
    }
}
