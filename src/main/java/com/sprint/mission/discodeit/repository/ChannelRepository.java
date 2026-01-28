package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Optional<Channel> findById(UUID channelId);
    Optional<Channel> findByName(String channelName);
    List<Channel> findAll();
    List<Channel> findAllByUserId(UUID userId);
    void save(Channel channel);
    void deleteById(UUID channelId);
}
