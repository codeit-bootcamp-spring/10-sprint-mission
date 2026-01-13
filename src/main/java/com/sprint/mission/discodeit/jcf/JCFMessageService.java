package com.sprint.mission.discodeit.jcf;

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
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(UUID userId, UUID channelId, String content) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        Message message = new Message(user, channel, content);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        validateExistence(data, id);
        return data.get(id);
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID messageId) {
        validateExistence(data, messageId);
        Message message = findById(messageId);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public void delete(UUID id) {
        validateExistence(data, id);
        data.remove(id);
    }

    private void validateExistence(Map<UUID, Message> data, UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("실패 : 존재하지 않는 메시지 ID입니다.");
        }
    }
}
