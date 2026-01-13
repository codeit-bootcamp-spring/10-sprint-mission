package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    void create();

    Optional<Channel> read(UUID id);

    Optional<ArrayList<Channel>> readAll();

    void update(Channel chnData);

    Channel delete(UUID id);
}
