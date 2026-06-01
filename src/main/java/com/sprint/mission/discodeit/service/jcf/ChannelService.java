package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ChannelService {

  Channel createChannel(String name, String desc, ChannelType type);

  void setUserService(UserService userService);

  Channel readChannel(UUID uuid);

  Channel updateChannel(UUID uuid, String name, String desc, ChannelType type);

  void deleteChannel(UUID uuid);

  List<Channel> readAllChannels();

  List<Channel> readChannelsbyUser(String userID);

  void userJoin(String userID, UUID channelID);

  void userLeave(String userID, UUID channelID);

  void deleteChannelbyName(String name);

  void save(Channel channel);


}
