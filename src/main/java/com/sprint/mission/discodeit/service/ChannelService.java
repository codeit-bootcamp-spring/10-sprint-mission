package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.listener.ChannelLifecycleListener;

import java.util.*;

public interface ChannelService {
    Channel createChannel(String channelName, User owner);

    Channel findChannelById(UUID channelId);
    List<Channel> findAllChannels();
//    Channel findChannelByName(String channelName);
//    Channel findChannelByOwner(User owner);

    Channel updateChannel(UUID channelId, String newChannelName);

    void deleteChannel(UUID channelId);
    void deleteChannelsByOwnerId(UUID ownerId);

    void addListener(ChannelLifecycleListener listener);
}