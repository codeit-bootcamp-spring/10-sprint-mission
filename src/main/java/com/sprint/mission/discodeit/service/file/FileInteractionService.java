package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FileInteractionService implements InteractionService {

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    public FileInteractionService(
            UserService userService,
            ChannelService channelService,
            MessageService messageService) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
    }

    @Override
    public void join(UUID userId, UUID channelId) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        Objects.requireNonNull(user, "유저 객체가 유효하지 않습니다.");
        Objects.requireNonNull(channel, "채널 객체가 유효하지 않습니다.");

        user.joinChannel(channel);
        channel.addUser(user);

        userService.update(userId, null, null, null);
        channelService.update(channelId, null, null);
    }

    @Override
    public void leave(UUID userId, UUID channelId) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        Objects.requireNonNull(user, "유저 객체가 유효하지 않습니다.");
        Objects.requireNonNull(channel, "채널 객체가 유효하지 않습니다.");

        user.leaveChannel(channel);
        channel.removeUser(user);

        userService.update(userId, null, null, null);
        channelService.update(channelId, null, null);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = userService.findById(userId);
        Objects.requireNonNull(user, "유저 객체가 유효하지 않습니다.");

        user.getChannels().forEach(channel -> {
            channel.removeUser(user);
            channelService.update(channel.getId(), null, null);
        });

        userService.delete(user.getId());
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = channelService.findById(channelId);
        Objects.requireNonNull(channel, "삭제할 채널 객체는 null일 수 없습니다.");

        List<Message> messagesToDelete = new ArrayList<>(messageService.findAllByChannelId(channel.getId()));
        messagesToDelete.forEach(message -> messageService.delete(message.getId()));

        channel.getUsers().forEach(user -> {
            user.leaveChannel(channel);
            userService.update(user.getId(), null, null, null);
        });

        channelService.delete(channel.getId());
    }
}