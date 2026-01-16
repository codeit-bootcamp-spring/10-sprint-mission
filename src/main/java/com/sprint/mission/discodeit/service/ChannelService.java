package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(String name);
    Channel findById(UUID channelId);
    List<Channel> findAll();
    Channel update(UUID channelId, String name);
    void delete(UUID channelId);

    void joinChannel(UUID channelId, UUID userId);
    void leaveChannel(UUID channelId, UUID userId);


}
