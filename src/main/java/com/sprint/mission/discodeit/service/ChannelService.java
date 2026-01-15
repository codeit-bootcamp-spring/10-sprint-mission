package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(String channelName);

    Channel read(UUID id);

    List<Channel> readAll();

    Channel updateChannelname(UUID id, String name);

    void delete(UUID id);

    void joinUser(User user, Channel channel);

    void quitUser(User user, Channel channel);

    List<Channel> readUserChannelList(User user);
}
