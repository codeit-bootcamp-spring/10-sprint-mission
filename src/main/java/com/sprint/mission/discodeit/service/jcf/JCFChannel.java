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
    public void read(UUID id) {
        for(Channel channel : this.channels) {
            if (channel.getId() == id){
                System.out.println(channel);
                return;
            }
        }
    }

    @Override
    public void readAll() {
        channels.forEach(System.out::println);
    }

    @Override
    public Channel create(String channelName, String channelDescription) {
        Channel channel = new Channel(channelName, channelDescription);
        channels.add(channel);
        return channel;

    }

    @Override
    public void delete(Channel channel) {
        channels.remove(channel);
    }

    @Override
    public void update(Channel originchannel, Channel afterchannel) {
        if(originchannel == null || originchannel.getId() == null) {
            return;
        }

        for(Channel c : channels) {
            if (c.getId() == originchannel.getId()){
                c.updateChannelName(afterchannel.getChannelName());
                c.updateChannelDescription(afterchannel.getChannelDescription());

            }
        }
    }
}