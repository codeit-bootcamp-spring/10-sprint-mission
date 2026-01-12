package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.HashSet;
import java.util.UUID;

public class JCFChannel implements ChannelService {
    private static JCFChannel instance = null;
    private JCFChannel(){}
    public static JCFChannel getInstance(){
        if(instance == null){
            instance = new JCFChannel();
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
        return channels;
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
        this.find(id).updateChannelName(name);
        return this.find(id);
    }

    public Channel updateDesc(UUID id, String desc) {
        this.find(id).updateChannelDescription(desc);
        return this.find(id);
    }
}