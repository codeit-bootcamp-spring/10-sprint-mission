package com.sprint.mission.discodeit.repository;


import com.sprint.mission.discodeit.entity.Channel;

import java.util.UUID;
import java.util.List;

public interface ChannelRepository {
    Channel createChannel(Channel channel);
    Channel findChannel(UUID id);
    List<Channel> findAllChannel();
    void deleteChannel(UUID id);
    Channel updateChannel(UUID id, Channel channel);
    boolean existsByNameChannel(String channelName);
    Channel saveChannel(Channel channel);
    Channel findByUserId(UUID userId);
    String findAllUserInChannel(UUID channelId);

}
