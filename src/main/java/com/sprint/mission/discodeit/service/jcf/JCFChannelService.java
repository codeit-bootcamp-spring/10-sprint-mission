package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;


import java.util.*;

public class JCFChannelService implements ChannelService {
    List<Channel> data = new ArrayList<>();

    @Override
    public Channel createChannel(String name, String desc, Channel.CHANNEL_TYPE type) {
        if(name == null || desc == null || type == null){
            return null;
        }

        boolean isRedundant = data.stream().anyMatch(c -> name.equals(c.getName()));
        if(isRedundant){
            return null;
        }

        Channel channel = new Channel(type, name, desc);
        data.add(channel);
        return channel;
    }


    @Override
    public Channel readChannel(String name) {


        return data.stream()
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Channel readChannel(UUID id){
        return data.stream()
                .filter(c -> id.equals(c.getId())).findFirst().orElse(null);
    }

    @Override
    public boolean updateChannel(String name, String desc, Channel.CHANNEL_TYPE type) {
        Optional<Channel> target = data.stream()
                .filter(c -> name.equals(c.getName()))
                .findFirst();

        if(target.isEmpty()){
            return false;
        }

        Channel channel = target.get();
        if(name != null){
            channel.updateName(name);
        }
        if(desc != null){
            channel.updateDesc(desc);
        }
        if(type != null){
            channel.updateType(type);
        }

        return false;
    }

    @Override
    public boolean deleteChannel(String name) {
        if(name == null){
            return false;
        }

        Optional<Channel> target = data.stream().filter(c -> name.equals(c.getName())).findFirst();
        if(target.isEmpty()){
            return false;
        }

        data.remove(target.get());

        return true;
    }

    @Override
    public ArrayList<Channel> readAllChannels() {
        data.forEach(System.out::println);
        return (ArrayList<Channel>) data;
    }
}
