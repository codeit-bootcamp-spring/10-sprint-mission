package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Optional<Channel> find(UUID channelID);
    List<Channel> findAll();
    void deleteChannel(UUID channelID);
    Channel save(Channel channel);
}
