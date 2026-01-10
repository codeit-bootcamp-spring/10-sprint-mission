package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.ArrayList;
import java.util.UUID;

public interface ChannelService {
    public Channel createChannel(String name, String desc, Channel.CHANNEL_TYPE type);
    public Channel readChannel(String name);
    public Channel readChannel(UUID id);
    public boolean updateChannel(String name, String desc, Channel.CHANNEL_TYPE type);
    public boolean deleteChannel(String name);
    public ArrayList<Channel> readAllChannels();


}
