package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    ArrayList<Channel> list = new ArrayList<>();

    @Override
    public Channel createChannel(ChannelType type, String channelName, String channelDescription) {
        Channel channel = new Channel(type, channelName, channelDescription);
        list.add(channel);
        return channel;
    }

    @Override
    public Channel readChannel(UUID id) {
        for (Channel channel : list) {
            if(id.equals(channel.getId())){
                return channel;
            }
        }
        return null;
    }

    @Override
    public List<Channel> readAllChannel() {
        return list;
    }

    @Override
    public void updateChannel(UUID id, ChannelType type, String channelName, String channelDescription) {
        readChannel(id).updateChannelType(type);
        readChannel(id).updateChannelName(channelName);
        readChannel(id).updateChannelDescription(channelDescription);
    }

    @Override
    public void deleteChannel(UUID id) {
        list.remove(readChannel(id));
    }

    @Override
    public boolean isChannelDeleted(UUID id) {
        for (Channel channel : list) {
            if(id.equals(channel.getId())) {
                return false;
            }
        }
        return true;
    }
}
