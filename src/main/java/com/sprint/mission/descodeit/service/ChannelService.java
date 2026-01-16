package com.sprint.mission.descodeit.service;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;

import java.util.*;

public interface ChannelService {
    Channel create(String name);
    Channel joinUsers(UUID channelId,UUID ...userId);
    Channel findChannel(UUID channelId);
    List<Channel> findAllChannel();
    List<Channel> findAllChannelsByUserId(UUID userId);
    Channel update(UUID channelId, String newName);
    void delete(UUID channelId);
    public Map<UUID, Channel> loadChannel();
    public void saveChannel(Map<UUID, Channel> data);
}
