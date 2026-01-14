package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }

    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        data.add(channel);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        return data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow( () -> new NotFoundException("해당 ID의 채널을 찾을 수 없습니다"));
    }

    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(data);
    }

    @Override
    public List<User> getUserList(UUID channelId) {
        Channel channel = read(channelId);
        return channel.getUserList();
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = read(id);
        return channel.update(name);
    }

    @Override
    public void delete(UUID id) {
        Channel channel = read(id);
        data.remove(channel);
    }
}
