package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final UserService userService;
    private final List<Channel> data;

    public JCFChannelService(UserService userService) {
        this.userService = userService;
        this.data = new ArrayList<>();
    }

    @Override
    public Channel create(String name, User owner) {
        Channel channel = new Channel(name, owner);
        data.add(channel);
        return channel;
    }

    @Override
    public Channel read(UUID channelId) {
        return data.stream()
                .filter(ch -> ch.getId().equals(channelId))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(data);
    }

    @Override
    public Channel update(UUID channelId, String newName) {
        Channel channel = read(channelId);
        channel.update(newName);
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        data.removeIf(ch -> ch.getId().equals(channelId));
    }

    @Override
    public List<User> readUsers(UUID channelId) {
        Channel channel = read(channelId);
        return channel.getUsers()
                .stream()
                .map(userService::read)
                .toList();
    }

    @Override
    public void join(UUID channelId, UUID userId) {
        Channel channel = read(channelId);
        userService.read(userId);
        channel.join(userId);
    }
}
