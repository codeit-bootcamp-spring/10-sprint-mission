package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel create(String channelName);

    Optional<Channel> read(UUID id);

    ArrayList<Channel> readAll();

    Channel updateChannelname(UUID id, String name);

    void delete(UUID id);
}
