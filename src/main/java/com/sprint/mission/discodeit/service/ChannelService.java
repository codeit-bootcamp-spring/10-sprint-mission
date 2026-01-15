package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(ChannelType type, String channelName, String channelDescription);

    Channel readChannel(UUID id);

    List<Channel> readAllChannel();

    Channel updateChannel(UUID id, ChannelType type, String channelName, String channelDescription);

    void deleteChannel(UUID id);

    boolean isChannelDeleted(UUID id);

    void joinChannel(UUID channelId, UUID userId);

    void leaveChannel(UUID channelId, UUID userId);


}
