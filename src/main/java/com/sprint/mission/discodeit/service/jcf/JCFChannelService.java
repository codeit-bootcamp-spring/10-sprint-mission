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
        for (Channel channel : data){
            if (channel.getId().equals(id)) {
                {
                    return channel;
                }
            }
         }
        throw new IllegalArgumentException("Channel not found" + id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
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
    public void setName(UUID id, String name) {

        Channel channel = findById(id); // null 가능

        Optional.ofNullable(channel)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + id));

        channel.setName(name);
        channel.touch();
    }

    @Override
    public void setDescription(UUID id, String description) {
        Channel channel = findById(id);

        Optional.ofNullable(channel)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + id));

        channel.setDescription(description);
        channel.touch();
    }


    @Override
    public void delete(UUID id) {
        data.removeIf(channel -> channel.getId().equals(id));
    }
}
