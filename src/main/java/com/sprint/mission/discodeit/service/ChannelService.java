package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(String name);

    Channel findById(UUID channelId);

    List<Channel> findAllChannel();

    Channel updateName(UUID channelId, String name);

    Channel addUser(UUID channelId, UUID userId);

    boolean deleteUser(UUID channelId, UUID userId);

    boolean delete(UUID channelId);

    boolean isUserInvolved(UUID channelId, UUID userId);
}
