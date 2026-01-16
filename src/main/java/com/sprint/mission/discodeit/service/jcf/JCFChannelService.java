package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final List<Channel> data;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }

    @Override
    public Channel createChannel(String name, String description) {
        Channel channel = new Channel(name, description);
        data.add(channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"+id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void updateChannel(UUID id, String name, String description) {
        Channel channel = findById(id);
        Optional.ofNullable(name).ifPresent(channel::updateChannelName);
        Optional.ofNullable(description).ifPresent(channel::updateChannelDescription);

        channel.touch();
    }

    @Override
    public void delete(UUID id) {
        Channel channel = findById(id);
        data.remove(channel);
    }

}
