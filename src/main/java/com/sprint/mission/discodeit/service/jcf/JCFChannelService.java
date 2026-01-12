package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.HashSet;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private static JCFChannelService instance = null;
    private JCFChannelService(){}
    public static JCFChannelService getInstance(){
        if(instance == null){
            instance = new JCFChannelService();
        }
        return instance;
    }

    HashSet<Channel> channels = new HashSet<>();

    @Override
    public Channel find(UUID id) {
        return channels.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Channel not found: id = " + id));
    }

    @Override
    public HashSet<Channel> findAll() {
        HashSet<Channel> newChannels = new HashSet<>();
        for(Channel channel : channels){
            newChannels.add(channel);
        }
        return newChannels;
    }

    @Override
    public Channel create(String channelName, String channelDescription) {
        Channel channel = new Channel(channelName, channelDescription);
        channels.add(channel);
        return channel;

    }

    @Override
    public void delete(UUID id) {
        channels.remove(find(id));
    }

    @Override
    public Channel updateName(UUID id, String name) {
        this.find(id)
                .updateChannelName(name);
        return this.find(id);
    }

    public Channel updateDesc(UUID id, String desc) {
        this.find(id)
                .updateChannelDescription(desc);
        return this.find(id);
    }
}