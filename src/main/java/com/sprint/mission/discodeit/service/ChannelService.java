package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(String name);
    Channel read(UUID id);
    List<Channel> readAll();
    List<Channel> getChannelsByUser(UUID userId);
    Channel update(UUID id, String name);
    void delete(UUID id);
    void deleteUserInChannels(UUID userId);
}
