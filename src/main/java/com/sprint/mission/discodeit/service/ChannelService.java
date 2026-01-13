package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    void save(Channel channel);

    Optional<Channel> findById(UUID id);

    List<Channel> findAll();

    void delete(Channel channel);
}
