package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.TargetType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.RoleService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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

    Set<Channel> channels = new HashSet<>();

    @Override
    public Channel find(UUID id) {
        return channels.stream()
                .filter(channel -> id.equals(channel.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Channel not found: id = " + id));
    }

    @Override
    public Set<Channel> findAll() {
        Set<Channel> newChannels = new HashSet<>();
        newChannels.addAll(channels);
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
    public Channel update(UUID id, String name, String desc) {
        Channel willUpdate = this.find(id);
        Optional.ofNullable(name)
                .ifPresent(willUpdate::updateChannelName);
        Optional.ofNullable(desc)
                .ifPresent(willUpdate::updateChannelDescription);
        return willUpdate;
    }

}