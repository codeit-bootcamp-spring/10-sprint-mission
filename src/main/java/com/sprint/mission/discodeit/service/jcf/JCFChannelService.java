package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;

    public JCFChannelService(){
        this.data = new HashMap<>();
    }

    @Override
    public Channel create(Channel channel){
        data.put(channel.getId(),channel); //data에 key value 값으로 넣음.
        return channel;
    }

    @Override
    public Channel findById(UUID id){
        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = data.get(id);
        if (channel != null) {
            channel.setChannelName(name);
        }
        return channel;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
