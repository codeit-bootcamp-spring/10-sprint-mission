package com.sprint.mission.discodeit.repository;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;
import java.util.List;

public interface ChannelRepository {

    void userAddChannel(UUID channelId, UUID userId);
    Channel createChannel(Channel channel);
    Channel findChannel(UUID id);
    List<Channel> findAllChannel();
    void deleteChannel(UUID id);
    boolean existsByNameChannel(String channelName);
    Channel findByUserId(UUID userId);
    String findAllUserInChannel(UUID channelId);

}