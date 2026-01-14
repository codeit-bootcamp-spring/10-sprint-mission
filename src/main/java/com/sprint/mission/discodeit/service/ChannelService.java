package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.TargetType;

import java.util.Set;
import java.util.UUID;

public interface ChannelService {
    public Channel find(UUID id);
    public Set<Channel> findAll();
    public Channel create(String channelName, String channelDescription);
    public void delete(UUID id);
    public Channel update(UUID id, String name, String desc);
    public void userJoinChannel(UUID channelId, UUID userId, TargetType type);
    public void userLeaveChannel(UUID channelId, UUID userId);
}
