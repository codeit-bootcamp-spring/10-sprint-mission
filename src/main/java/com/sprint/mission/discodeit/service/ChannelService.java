package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(String channelName);

    Channel read(UUID id);

    List<Channel> readAll();

    Channel updateChannelname(UUID id, String name);

    void delete(UUID id);

//    void joinUser(UUID userId, UUID channelId);
//
//    void quitUser(UUID userId, UUID channelId);

}
