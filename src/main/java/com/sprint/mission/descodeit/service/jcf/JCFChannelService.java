package com.sprint.mission.descodeit.service.jcf;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;
import com.sprint.mission.descodeit.service.ChannelService;

import java.util.*;


public class JCFChannelService implements ChannelService {
    private Map<UUID, Channel> data;

    public JCFChannelService(){
        this.data = new HashMap<>();
    }

    @Override
    public void create(Channel channel) {
        data.put(channel.getId(), channel);
    }

    @Override
    public Channel findCannel(UUID channelID) {
        System.out.println(data.get(channelID));
        return data.get(channelID);
    }

    @Override
    public Set<UUID> findAllChannel() {
        System.out.println("[채널 전체 조회]");
        for(UUID id : data.keySet()){
            System.out.println(data.get(id));
        }
        return data.keySet();
    }

    @Override
    public void update(UUID channelID, String newName) {
        data.get(channelID).updateChannel(newName);
    }

    @Override
    public void delete(UUID channelID) {
        data.remove(channelID);
    }
}

