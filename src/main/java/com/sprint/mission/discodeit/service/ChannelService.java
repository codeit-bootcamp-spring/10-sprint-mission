package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

    // Create
    Channel createChannel(String name, Channel.ChannelVisibility visibility);

    // Read
    Channel findById(UUID channelId);
    List<Channel> findAll();

    // Update
    Channel updateChannel(UUID channelId, String newName, Channel.ChannelVisibility newVisibility);

    // Delete
    void deleteChannel(UUID channelId);

    // Setter
    void setMessageService(MessageService messageService);
}