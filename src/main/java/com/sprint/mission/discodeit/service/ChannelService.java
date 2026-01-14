package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String title, String description);
    Channel getChannel(UUID uuid);
    Optional<Channel> findChannelByTitle(String title);
    List<Channel> findAllChannels();
    Channel updateChannel(UUID uuid, String title, String description);
    void deleteChannel(UUID uuid);
    void deleteChannelByTitle(String title);
    void joinChannel(UUID channelId, UUID userId);
    void leaveChannel(UUID channelId, UUID userId);
}
