package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    Channel find(UUID channelID);
    List<Channel> findAll();
    void deleteChannel(Channel channel);
    Channel save(Channel channel);
}
