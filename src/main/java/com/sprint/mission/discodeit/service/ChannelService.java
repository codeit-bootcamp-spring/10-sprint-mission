package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.HashSet;
import java.util.UUID;

public interface ChannelService {
    public Channel find(UUID id);
    public HashSet<Channel> findAll();
    public Channel create(String channelName, String channelDescription);
    public void delete(UUID id);
    public void update(UUID id, String str, boolean isChangingName);
}
