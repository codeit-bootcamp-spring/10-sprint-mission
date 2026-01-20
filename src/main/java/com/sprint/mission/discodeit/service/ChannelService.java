package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    // Create
    Channel createChannel(String name, String description, Channel.ChannelType visibility);

    // Read
    Channel findById(UUID channelId);
    List<Channel> findAll();

    // Update
    Channel updateChannel(UUID channelId, String newName, String description, Channel.ChannelType newVisibility);

    // Delete
    void deleteChannel(UUID channelId);

    // logic
    void save(Channel channel);
}