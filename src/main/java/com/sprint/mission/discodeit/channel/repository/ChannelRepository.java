package com.sprint.mission.discodeit.channel.repository;

import com.sprint.mission.discodeit.channel.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel);
    Optional<Channel> findById(UUID id);
    List<Channel> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);
}
