package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    // 생성자
    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel create(String channelName) {
        Channel channel = new Channel(channelName);
        this.data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        Channel channel = Optional.ofNullable(data.get(id)).orElseThrow(() -> new NoSuchElementException());
        return channel;
    }

    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public Channel updateChannelname(UUID id, String name) {
        this.read(id).updateChannelName(name);
        return this.read(id);
    }

    @Override
    public void delete(UUID id) {
        this.read(id);
        this.data.remove(id);
    }

    @Override
    public void joinUser(User user, Channel channel) {
        channel.getUserList().add(user);
        user.getChannelList().add(channel);
    }

    @Override
    public void quitUser(User user, Channel channel) {
        channel.getUserList().remove(user);
        user.getChannelList().remove(channel);
    }

    @Override
    public List<Channel> readUserChannelList(User user) {
        return user.getChannelList();
    }
}