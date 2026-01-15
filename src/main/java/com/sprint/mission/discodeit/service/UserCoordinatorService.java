package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserCoordinatorService {
    List<Channel> getChannels(UUID id);//유저의 채널목록
    List<User> findAllMembers(UUID id, UUID memberId);
    Channel addMembers(UUID id, UUID ownerId, List<UUID> memberIds);
    Channel removeMembers(UUID id, UUID ownerId, List<UUID> memberIds);
    Channel removeMember(UUID id, UUID memberId);
    Channel getChannelByIdAndMemberId(UUID id, UUID memberId);
    Channel getOrCreateDirectChannelByChatterIds(UUID senderId, UUID receiverId);
}
