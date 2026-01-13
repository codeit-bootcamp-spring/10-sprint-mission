package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.UUID;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    public JCFChannelService(){
        this.data = new HashMap<>();
    }

    @Override
    public void create(Channel channel){
        data.put(channel.getId(), channel);
    }

    @Override
    public Channel readById(UUID id){
        return data.get(id);
    }

    @Override
    public List<Channel> readAll(){
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(Channel channel){
        Channel existingChannel = data.get(channel.getId());

        if (existingChannel != null){
            existingChannel.update(channel.getName(), channel.getDescription());
        }
    }

    @Override
    public void delete(UUID id){
        data.remove(id);
    }
}
