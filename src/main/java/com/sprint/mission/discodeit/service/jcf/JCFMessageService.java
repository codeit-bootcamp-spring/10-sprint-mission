package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;

import java.util.*;

public class JCFMessageService implements MessageService {
    final ArrayList<Message> list;
    private final ChannelService channelService;
    private final UserService userService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.list = new ArrayList<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(String content, UUID channelId, UUID userId) {
        Validators.validationMessage(content);
        Channel channel = channelService.readChannel(channelId);
        User user = userService.readUser(userId);
        Message message = new Message(content, channel, user);

        channel.getMessages().add(message);
        user.getMessages().add(message);

        list.add(message);
        return message;
    }

    @Override
    public Message readMessage(UUID id) {
        return validateExistenceMessage(id);
    }

    @Override
    public List<Message> readAllMessage() {
        return list;
    }

    @Override
    public Message updateMessage(UUID id, String content) {
        Message message = validateExistenceMessage(id);
        Optional.ofNullable(content)
                .ifPresent(cont -> {Validators.requireNotBlank(cont, "content");
            message.updateContent(content);
        });

        return message;
    }

    public void deleteMessage(UUID id) {
        Message message = validateExistenceMessage(id);
        Channel channel = message.getChannel();
        User user = message.getUser();

        channel.getMessages().removeIf(m -> id.equals(m.getId()));
        user.getMessages().removeIf(m -> id.equals(m.getId()));
        list.removeIf(m -> id.equals(m.getId()));
    }

    public List<Message> readMessagesByChannel(UUID channelId) {
        Channel channel = channelService.readChannel(channelId);
        return channel.getMessages();
    }

    public List<Message> readMessagesByUser(UUID userId) {
        User user = userService.readUser(userId);
        return user.getMessages();
    }

    private Message validateExistenceMessage(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return list.stream()
                .filter(message -> id.equals(message.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("메세지 id는 존재하지 않습니다."));
    }

}
