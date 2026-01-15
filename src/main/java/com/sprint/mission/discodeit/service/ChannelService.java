package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel createChannel(String channelName);

    Channel findChannel(UUID channelId);

    List<Channel> findAllChannel();

    Channel memberAddChannel(UUID channelId, UUID userId);

    Channel memberRemoveChannel(UUID channelId, UUID userId);

    Channel nameUpdateChannel(UUID channelId, String channelName);

    Channel deleteChannel(UUID channelId);
}
