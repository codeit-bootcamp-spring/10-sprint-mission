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
    public Channel read(UUID id) {
        return channels.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public HashSet<Channel> readAll() {
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
        channels.remove(channels.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElse(null)
                );
    }

    @Override
    public void update(UUID id, String str, boolean isChangingName) {
        channels.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .ifPresent(channel ->{
                    if(isChangingName){
                        channel.updateChannelName(str);
                    }else{
                        channel.updateChannelDescription(str);
                    }
                });
    }




}