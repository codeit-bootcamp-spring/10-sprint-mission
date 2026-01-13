package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String title, String description);
    Optional<Channel> findChannel(UUID uuid);
    Optional<Channel> findChannelByTitle(String title);
    List<Channel> findAllChannels();
    Channel updateChannel(UUID uuid, String title, String description);
    void deleteChannel(UUID uuid);
    void deleteChannelByTitle(String title);
    void joinChannel(Channel channel, User user);
    void leaveChannel(Channel channel, User user);
}
