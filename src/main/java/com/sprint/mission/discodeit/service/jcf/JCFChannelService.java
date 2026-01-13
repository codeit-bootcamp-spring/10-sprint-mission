package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
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
    public Channel create(Channel channel) {
        data.add(channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        for (Channel channel : data) {
            if (channel.getId().equals(id)) {
                return channel;
            }
        }
        return null; //
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public Channel update(UUID id, String name, String description) {
        for (Channel channel : data) {
            if (channel.getId().equals(id)) {
                channel.update(id,name, description); // Channel 엔티티의 update 메서드
                return channel;
            }
        }
        throw new IllegalArgumentException("Channel not found: " + id);
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(channel -> channel.getId().equals(id));
    }
}
