package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final UserService userService;
    private final Map<UUID, Channel> data;

    public JCFChannelService(UserService userService) {
        this.userService = userService;
        this.data = new HashMap<>();
    }

    @Override
    public Channel createChannel(String name, UUID owner) {
        userService.findUserById(owner);
        Channel channel = new Channel(name, owner);

        data.put(channel.getId(), channel);
        userService.joinChannel(channel.getId(), owner);
        return channel;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new RuntimeException("존재하지 않는 채널입니다.");
        }

        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel updateChannelName(UUID channelId, String newName) {
        Channel channel = findChannelById(channelId);
        channel.updateChannelName(newName);
        return channel;
    }

    @Override
    public void deleteChannel(UUID channelId) {
        userService.leaveChannel(channelId);
        data.remove(channelId);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        findChannelById(channelId);
        userService.joinChannel(channelId, userId);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        findChannelById(channelId);
        userService.leaveChannel(channelId, userId);
    }
}
