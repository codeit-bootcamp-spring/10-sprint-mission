package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
        this.data = new HashMap<>();
    }

    public Message create(UUID userId, UUID channelId, String content) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        Message message = new Message(user, channel, content);
        data.put(message.getId(), message);
        return message;
    }

    public Message findById(UUID messageId) {
        Message message = data.get(messageId);
        if (message == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
        }
        return data.get(messageId);
    }

    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    public Message update(UUID messageId, String content) {
        Message message = findById(messageId);
        message.updateContent(content);
        return message;
    }

    public void delete(UUID messageId) {
        findById(messageId);
        data.remove(messageId);
    }

    public UserService getUserService() {
        return userService;
    }

    public ChannelService getChannelService() {
        return channelService;
    }
}
