package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final ChannelService channelService;
    private final Map<UUID, List<Message>> data;

    public JCFMessageService(ChannelService channelService) {
        this.channelService = channelService;
        this.data = new HashMap<>();
    }

    @Override
    public Message create(UUID channelId, UUID userId, String msg) {
        channelService.read(channelId);
        channelService.readUsers(channelId)
                .stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(RuntimeException::new);

        Message message = new Message(channelId, userId, msg);

        data.computeIfAbsent(channelId, id -> new ArrayList<>()).add(message);

        return message;
    }

    @Override
    public List<Message> read(UUID channelId) {
        channelService.read(channelId);
        return data.getOrDefault(channelId, Collections.emptyList());
    }

    @Override
    public Message update(UUID channelId, UUID userId, UUID messageId, String newMessage) {
        Message message = findMessage(channelId, userId, messageId);
        message.update(newMessage);
        return message;
    }

    @Override
    public void delete(UUID channelId, UUID userId, UUID messageId) {
        Message message = findMessage(channelId, userId, messageId);
        List<Message> messageList = data.getOrDefault(channelId, Collections.emptyList());
        messageList.remove(message);
    }

    private Message findMessage(UUID channelId, UUID userId, UUID messageId) {
        channelService.read(channelId);
        channelService.readUsers(channelId)
                .stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(RuntimeException::new);

        List<Message> messageList = data.getOrDefault(channelId, Collections.emptyList());

        return messageList.stream()
                .filter(m -> m.getId().equals(messageId))
                .filter(m -> m.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}
