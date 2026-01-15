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
    public Optional<Channel> findById(UUID id) {
        return data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void setName(UUID id, String name) {

    }

    @Override
    public void setDescription(UUID id, String description) {

    }

//    @Override
//    public Channel update(UUID id, String name, String description) {
//        for (Channel channel : data) {
//            if (channel.getId().equals(id)) {
//                channel.update(name, description); // Channel 엔티티의 update 메서드
//                return channel;
//            }
//        }
//        throw new IllegalArgumentException("Channel not found: " + id);
//    }


    @Override
    public void updateChannel(UUID id, String name, String description) {
        Channel channel = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + id));

        Optional.ofNullable(name).ifPresent(channel::setName);
        Optional.ofNullable(description).ifPresent(channel::setDescription);

        channel.touch();
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(channel -> channel.getId().equals(id));
    }
}
