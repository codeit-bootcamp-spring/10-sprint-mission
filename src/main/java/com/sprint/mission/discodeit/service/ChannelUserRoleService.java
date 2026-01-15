package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.ChannelUserRole;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.ChannelRole;

import java.util.List;
import java.util.UUID;

public interface ChannelUserRoleService {
    // Create (채널 입장)
    ChannelUserRole addChannelUser(UUID channelId, UUID userId, ChannelRole role);

    // Read (특정 채널의 모든 참가자 조회 - User 정보만)
    List<User> findUsersByChannelId(UUID channelId);

    // Read (특정 채널에서의 내 정보 조회 - Role 포함)
    // 수정/삭제 시 해당 객체를 찾기 위해서도 필요
    ChannelUserRole findChannelUser(UUID channelId, UUID userId);

    // Update (참가자 권한 수정)
    ChannelUserRole updateChannelRole(UUID channelId, UUID userId, ChannelRole newRole);

    // Delete (채널 나가기 / 강퇴)
    void deleteChannelUser(UUID channelId, UUID userId);

    void deleteAllAssociationsByUserId(UUID userId); // User-Delete 때문에 필요 (추후 구현)
}