package com.sprint.mission.repository;

import com.sprint.mission.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel);

    Optional<Channel> findById(UUID id);

    List<Channel> findByUserId(UUID userId);

    List<Channel> findAll();

//    Channel update(UUID id, String name);

    void deleteById(UUID id);
}
