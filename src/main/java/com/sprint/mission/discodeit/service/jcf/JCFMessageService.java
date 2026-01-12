package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
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

        if (userService.findById(userId) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if(channelService.findById(channelId) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        Message message = new Message(userId, channelId, content);
        data.put(message.getId(), message);
        return message;
    }

    public Message findById(UUID messageId) {
        return data.get(messageId);
    }

    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    public void update(UUID messageId, String content) {
        Message message = data.get(messageId);
        message.updateContent(content);
    }

    public void delete(UUID messageId) {
        data.remove(messageId);
    }

    public UserService getUserService() {
        return userService;
    }

    public ChannelService getChannelService() {
        return channelService;
    }
}
