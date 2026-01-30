package com.sprint.mission.discodeit.repository;


import com.sprint.mission.discodeit.entity.Channel;
import java.util.UUID;
import java.util.List;


public interface ChannelRepository {

    Channel saveChannel(Channel channel);
    Channel createChannel(Channel channel);
    Channel findChannel(UUID channelId);
    List<Channel> findAllChannel();
    void deleteChannel(UUID id);

}