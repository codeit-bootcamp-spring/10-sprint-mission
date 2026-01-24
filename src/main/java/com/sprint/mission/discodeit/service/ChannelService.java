package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.PermissionLevel;
import com.sprint.mission.discodeit.entity.Role;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ChannelService {
    public Channel find(UUID id);
    public Set<Channel> findAll();
    public Channel create(String channelName, String channelDescription);
    public void delete(UUID channelID, UUID userID);
    public Channel update(UUID id, String name, String desc);
    public Channel update(UUID id, List<UUID> roles, List<UUID> messages);
    public void printChannel(UUID id);
    public void updateUserRole(UUID channelID, UUID willChangeUserID, PermissionLevel roleName, UUID tryingUserID);
    public Message addMessage(UUID channelID, UUID userID, String msg);
}
