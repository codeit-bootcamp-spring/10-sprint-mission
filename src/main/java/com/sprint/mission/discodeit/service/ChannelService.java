package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String name, String description);
    Optional<Channel> findById(UUID id);
    List<Channel> findAll();
    void setName(UUID id, String name);
    void setDescription(UUID id, String description);
    void updateChannel(UUID id, String name, String description);
    void delete(UUID id);

}
