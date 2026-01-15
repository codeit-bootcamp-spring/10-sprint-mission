package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String name, UUID ownerId);
    Channel findChannelById(UUID channelId);
    List<Channel> findAll();
    Channel updateChannelName(UUID channelId, UUID userId, String name);
    void deleteChannel(UUID channelId, UUID userId);
    void joinChannel(UUID channelId, UUID userId);
    void leaveChannel(UUID channelId, UUID userId);
    List<User> getMembers(UUID channelId);
    List<Channel> getJoinedChannels(UUID userId);
}
