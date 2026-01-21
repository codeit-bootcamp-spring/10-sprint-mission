package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ChannelService {
    public Channel createChannel(String name, String desc, Channel.CHANNEL_TYPE type);
    public void setUserService(UserService userService);
    public Channel readChannel(UUID uuid);
    public Channel updateChannel(UUID uuid, String name, String desc, Channel.CHANNEL_TYPE type);
    public void deleteChannel(UUID uuid);
    public List<Channel> readAllChannels();
    public Set<Channel> readChannelsbyUser(String userID);
    public void userJoin(String userID, UUID channelID);
    public void userLeave(String userID, UUID channelID);
    public void deleteChannelbyName(String name);
    public void save(Channel channel);





}
