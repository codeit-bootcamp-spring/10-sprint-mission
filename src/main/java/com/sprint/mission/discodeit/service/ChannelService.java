package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.IsPrivate;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    // Create
    Channel create(String name, IsPrivate isPrivate, UUID ownerId);

    // Read
    Channel findById(UUID id);

    // ReadAll
    List<Channel> readAll();

    // Update
    Channel update(UUID id, String name, IsPrivate isPrivate, User owner);

    // 채널 참여
    void joinChannel(UUID userId, UUID channelId);

    List<Message> getChannelMessages(UUID channelId);

    List<User> getChannelUsers(UUID channelId);

    // Delete
    void delete(UUID id);


}
