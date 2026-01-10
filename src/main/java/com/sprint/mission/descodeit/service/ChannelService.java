package com.sprint.mission.descodeit.service;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;

import java.util.*;

public interface ChannelService {
    void create(Channel channel);
    Channel findCannel(UUID channelID);
    Set<UUID> findAllChannel();
    void update(UUID channelID, String newName);
    void delete(UUID channelID);
}
