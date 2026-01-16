package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
    private final List<Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new ArrayList<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(UUID channelId, UUID senderId, String text) {
        Channel channel = channelService.read(channelId);
        User sender = userService.read(senderId);

        Message message = new Message(channel, sender, text);
        data.add(message);

        return message;
    }

    @Override
    public Message read(UUID id) {
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElseThrow( () -> new NotFoundException("해당 ID의 메시지를 찾을 수 없습니다"));
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(data);
    }

    @Override
    public List<Message> getMessagesByUser(UUID userId) {
        userService.read(userId);
        return data.stream()
                .filter(message -> message.getSender().getId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        channelService.read(channelId);
        return data.stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    @Override
    public Message update(UUID id, String text) {
        Message message = read(id);
        return message.update(text);
    }

    @Override
    public void delete(UUID id) {
        Message message = read(id);
        data.remove(message);

    }

    @Override
    public void deleteMessageByUserId(UUID userId) {
        data.removeIf(message -> message.getSender().getId().equals(userId));
    }

    @Override
    public void deleteMessageByChannelId(UUID channelId) {
        data.removeIf(message -> message.getSender().getId().equals(channelId));
    }

}



