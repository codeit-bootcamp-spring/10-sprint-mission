package com.sprint.mission.descodeit.repository;

import com.sprint.mission.descodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    Channel findById(UUID channelId);
    List<Channel> findAll();
    void save(UUID channelId , Channel channel);
    void delete(UUID channelId);
}
