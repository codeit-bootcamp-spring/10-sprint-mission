package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String channelName);

    List<Channel> getChannelList();

    void updateChannelName(UUID channelId, String newChannelName);

    void joinChannel(UUID channelId, User user);

    void leaveChannel(UUID channelId, User user);

    void deleteChannel(UUID channelId);
}
