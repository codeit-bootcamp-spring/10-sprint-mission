package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelPermission;
import com.sprint.mission.discodeit.entity.PermissionTarget;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

    // Create
    Channel createChannel(String name, boolean isPublic);

    // Read
    Optional<Channel> findById(UUID channelId);

    List<Channel> findChannelsAccessibleBy(User user);

    List<Channel> findAll();

    // Update
    void updateChannel(UUID channelId, String name);
    void updateChannelVisibility(UUID channelId, boolean isPublic);

    // Permission Management
    void grantPermission(UUID channelId, UUID targetId, PermissionTarget type);

    void revokePermission(UUID channelId, UUID targetId);
    void removePermissionsByTargetId(UUID targetId);

    // Delete
    void deleteChannel(UUID channelId);

    // Setter
    void setMessageService(MessageService messageService);
}