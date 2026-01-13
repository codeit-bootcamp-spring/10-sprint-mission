package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel addChannel(String name, String description, UUID ownerId, boolean openType);
    Channel updateChannelName(UUID id, String name, UUID ownerId);
    Channel updateChannelDescription(UUID id, String description, UUID ownerId);
    Channel addMembers(UUID id, UUID ownerId, List<UUID> memberIds);
    Channel removeMembers(UUID id, UUID ownerId, List<UUID> memberIds);
    Channel removeMember(UUID id, UUID memberId);
    Channel getChannelByIdAndMemberId(UUID id, UUID memberId);
    List<Channel> findAllChannels();
    List<User> findAllMembers(UUID id, UUID memberId);
    void deleteChannelById(UUID id, UUID ownerId);
}
