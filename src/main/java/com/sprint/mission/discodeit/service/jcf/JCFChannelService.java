package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private UserService userService;
    private final List<Channel> data = new ArrayList<>();

    public JCFChannelService(UserService userService) {
        this.userService = userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Channel createChannel(String channelName, ChannelType channelType, String description) {
        Channel channel = new Channel(channelName, channelType, description);
        data.add(channel);
        return channel;
    }

    @Override
    public Channel getChannel(UUID channelId) {
        return findById(channelId);
    }

    @Override
    public List<Channel> getAllChannels() {
        return List.copyOf(data);
    }

    @Override
    public Channel updateChannel(UUID channelId, String channelName, ChannelType channelType, String description) {
        Channel oldChannel = findById(channelId);
        oldChannel.updateChannelName(channelName);
        oldChannel.updateDescription(description);
        if(!channelType.getValue().equals(oldChannel.getChannelType().getValue())) {
            oldChannel.updateChannelType();
        }
        return oldChannel;
    }

    @Override
    public Channel deleteChannel(UUID channelId) {
        Channel target = findById(channelId);
        target.getUsers().forEach(user -> user.removeChannel(target));
        data.remove(target);
        return target;
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.getUser(userId);

        channel.addUser(user);
        user.addChannel(channel);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.getUser(userId);

        channel.removeUser(user);
        user.removeChannel(channel);
    }

    private Channel findById(UUID id) {
        return data.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 채널이 존재하지 않습니다."));
    }

    public ChannelType changeChannelType(Channel channel) {
        channel.updateChannelType();
        return channel.getChannelType();
    }
}
