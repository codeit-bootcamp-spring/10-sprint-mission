package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.UUID;

public interface ChannelService {
    UUID createChannel(String title, String description);
    void updateChannel(UUID id, String title, String description);
    void deleteChannel(UUID id);
    Channel getChannel(UUID id);
    Channel getChannel(String title);
    Iterable<Channel> getAllChannels();
}
