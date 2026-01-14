package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelList;

    public JCFChannelService(){
        this.channelList = new HashMap<>();
    }

    @Override
    public Channel createChannel(String name, String intro) {
        Channel newChannel = new Channel(name,intro);
        channelList.put(newChannel.getId(), newChannel);
        return newChannel;
    }


    @Override
    public Channel findChannelById(UUID channelId) {
        return channelList.get(channelId);
    }

    public List<Channel> findAllChannels () {return new ArrayList<>(channelList.values());}

    public void deleteChannel(UUID channelId){
        channelList.remove(channelId);
    }
}
