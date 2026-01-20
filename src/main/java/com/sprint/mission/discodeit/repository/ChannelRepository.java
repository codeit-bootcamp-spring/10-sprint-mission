package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel);
    Channel findChannelById(UUID channelId);
    List<Channel> findAllChannel();
    void delete(Channel channel);
}
