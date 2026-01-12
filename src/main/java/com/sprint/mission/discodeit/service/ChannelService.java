package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String title, String description);
    Optional<Channel> findChannel(UUID uuid);
    List<Channel> findAllChannels();
    Channel updateChannel(UUID uuid, String title, String description);
    void deleteChannel(UUID uuid);
}
