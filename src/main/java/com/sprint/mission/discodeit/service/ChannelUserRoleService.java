package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;

import java.util.*;

public interface ChannelUserRoleService {
    ChannelUserRole addChannelUser(UUID channelId, UUID userId, ChannelRole role);
    // 특정 채널에 참여 중인 모든 유저의 정보를 조회 (참여자 목록 UI 등을 위해 사용)
    List<User> findUsersByChannelId(UUID channelId);
    // 특정 유저가 특정 채널에서 어떤 권한(Role)을 가지고 있는지 조회 (권한 체크나 정보 수정 시 사용)
    ChannelUserRole findChannelUser(UUID channelId, UUID userId);
    // 특정 유저가 참여 중인 채널 목록 조회 (내가 가입한 채널 목록을 보여줄 때 사용)
    List<Channel> findChannelsByUserId(UUID userId);
    ChannelUserRole updateChannelRole(UUID channelId, UUID userId, ChannelRole newRole);
    // 유저-채널 관계를 제거함 (유저 -> 채널 나가기/강퇴시 사용)
    void deleteChannelUserAssociation(UUID channelId, UUID userId);
    void deleteAllAssociationsByUserId(UUID userId);
    void deleteAllAssociationsByChannelId(UUID channelId);
}