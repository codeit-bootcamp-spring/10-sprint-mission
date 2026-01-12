package com.sprint.mission.descodeit.service;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;

import java.util.*;

public interface ChannelService {
    Channel create(String name);
    Channel joinUsers(UUID channelID,UUID ...userID);
    Channel findChannel(UUID channelID);
    List<Channel> findAllChannel();
    Channel update(UUID channelID, String newName);
    boolean delete(UUID channelID);
}
