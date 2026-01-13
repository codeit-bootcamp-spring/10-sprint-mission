package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;

    public JCFChannelService(){
        this.data = new HashMap<>();
    }

    @Override
    public Channel create(String channelName, String type, User user){
        Channel channel = new Channel(channelName,type,user);
        data.put(channel.getId(),channel); //data에 key value 값으로 넣음.
        return channel;
    }

    @Override
    public Channel findById(Channel channel){
        if(data.get(channel.getId()) == null){
            throw new IllegalArgumentException("채널이 없습니다.");
        }
        return data.get(channel.getId());
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(Channel channel, String name) {
        Channel channels = data.get(channel.getId());
        if (channels == null) {
            throw new IllegalArgumentException("수정할 채널이 없습니다.");
        }
        channels.setChannelName(name);
        return channels;
    }

    @Override
    public void delete(Channel channel) {
        if(data.get(channel) == null){
            throw new IllegalArgumentException("삭제할 채널이 없습니다.");

        }
        data.remove(channel.getId());
    }
}
