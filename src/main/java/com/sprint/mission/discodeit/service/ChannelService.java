package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel createChannel(String channelName);

    Channel findChannel(UUID channelId);

    List<Channel> findAllChannel();

    Channel userAddChannel(UUID channelId, UUID userId);

    Channel userRemoveChannel(UUID channelId, UUID userId);

    Channel nameUpdateChannel(UUID channelId, String channelName);

    Channel deleteChannel(UUID channelId);

    Channel findByUserChannel(UUID userId);

    // **특정 채널 참가자 목록 조회**
    String findAllUserInChannel(UUID channelId);
}
