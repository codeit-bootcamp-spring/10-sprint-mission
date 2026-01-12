package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.ArrayList;
import java.util.UUID;

public interface ChannelService {
    public Channel createChannel(String name, String desc, Channel.CHANNEL_TYPE type);

    public Channel readChannel(UUID uuid);
    public Channel updateChannel(UUID uuid, String name, String desc, Channel.CHANNEL_TYPE type);
    public Channel deleteChannel(String name);
    public ArrayList<Channel> readAllChannels();


}
