package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel create(String name, String description, String type, boolean isPublic) {
        Channel newChannel = new Channel(name, description, type, isPublic);
        data.put(newChannel.getId(), newChannel);
        return newChannel;
    }

    @Override
    public Channel findById(UUID id){
        Channel channel = data.get(id);
        if (channel == null) {
            throw new NoSuchElementException("실패: 존재하지 않는 채널 ID");
        }
        return channel;
    }

    @Override
    public List<Channel> findAll(){
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, String name, String description, boolean isPublic){
        Channel channel = findById(id);
        channel.update(name, description, isPublic);
        return channel;
    }

    @Override
    public void delete(UUID id){
        findById(id);
        data.remove(id);
    }
}
