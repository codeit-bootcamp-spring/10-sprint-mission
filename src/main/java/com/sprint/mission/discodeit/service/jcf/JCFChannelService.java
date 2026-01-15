package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.userService = userService;
    }

    public Channel create(String name) {
        Channel channel = new Channel(name);
        data.put(channel.getId(), channel);
        return channel;
    }

    public Channel findById(UUID channelId) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 채널입니다. id= " + channelId);
        }
        return channel;
    }

    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    public Channel update(UUID channelId, String name) {
        Channel channel = findById(channelId);
        channel.updateName(name);
        return channel;
    }

    public void delete(UUID channelId) {
        findById(channelId);
        data.remove(channelId);
    }

    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);
        channel.addUser(user);
        user.addChannel(channel);
    }

    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);
        if (!channel.getUsers().contains(user)) {
            throw new IllegalArgumentException("해당 채널에 참여한 유저가 아닙니다.");
        }
        channel.removeUser(user);
        user.removeChannel(channel);
    }
    public List<User> findUsersByChannelId(UUID channelId) {
        Channel channel = findById(channelId);
        return channel.getUsers();
    }
}
