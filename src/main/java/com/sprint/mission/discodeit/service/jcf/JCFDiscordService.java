package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.DiscordService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class JCFDiscordService implements DiscordService {
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    public JCFDiscordService(UserService userService, ChannelService channelService, MessageService messageService) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
    }

    @Override
    public List<User> getUsersByChannel(UUID channelId) {
        channelService.read(channelId);
        return userService.getUsersByChannel(channelId);
    }

    @Override
    public List<Channel> getChannelsByUser(UUID userId) {
        userService.read(userId);
        return channelService.getChannelsByUser(userId);
    }

    @Override
    public List<Message> getMessagesByUser(UUID userId) {
        userService.read(userId);
        return messageService.getMessagesByUser(userId);
    }

    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        channelService.read(channelId);
        return messageService.getMessagesByChannel(channelId);
    }

    @Override
    public void deleteUser(UUID userId) {
        userService.read(userId);
        channelService.deleteUserInChannels(userId);
        messageService.deleteMessageByUserId(userId);
        userService.delete(userId);

    }

    @Override
    public void deleteChannel(UUID channelId) {
        channelService.read(channelId);
        channelService.deleteUserInChannels(channelId);
        messageService.deleteMessageByChannelId(channelId);
        channelService.delete(channelId);

    }

}
