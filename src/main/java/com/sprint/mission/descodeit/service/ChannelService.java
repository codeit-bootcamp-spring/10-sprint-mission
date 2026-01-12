package com.sprint.mission.descodeit.service;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;

import java.util.*;

public interface ChannelService {
    Channel create(String name);
    Channel joinUsers(Channel channel,User ...users);
    Channel findCannel(UUID channelID);
    List<Channel> findAllChannel();
    Channel update(UUID channelID, String newName);
    boolean delete(UUID channelID);
}
