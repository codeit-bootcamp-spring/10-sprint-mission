package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {

    void save(Channel channel);

    void delete(UUID channelId);

    List<Channel> loadAll();

    Channel loadById(UUID channelId);
}
