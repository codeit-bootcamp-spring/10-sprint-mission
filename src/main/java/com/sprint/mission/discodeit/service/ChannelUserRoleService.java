package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelUserRole;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.ChannelRole;

import java.util.List;
import java.util.UUID;

public interface ChannelUserRoleService {

    // Create
    /**
     * 채널 입장 (관계 생성)
     * 특정 유저를 채널의 구성원으로 추가하고 역할을 부여 (이미 참여 중인 경우 중복 입장을 막아야 함)
     * @param channelId 입장할 채널의 UUID
     * @param userId    입장하는 유저의 UUID
     * @param role      부여할 초기 권한 (예: MEMBER, ADMIN 등)
     * @return 생성된 채널-유저 관계 객체 (ChannelUserRole)
     * @throws IllegalArgumentException 유저나 채널이 없거나, 이미 해당 채널에 참여 중일 경우 발생
     */
    ChannelUserRole addChannelUser(UUID channelId, UUID userId, ChannelRole role);

    // Read
    /**
     * 채널 참가자 목록 조회
     * 특정 채널에 참여 중인 모든 유저(User)의 정보를 조회 (참여자 목록 UI 등을 위해 사용)
     * @param channelId 조회할 채널의 UUID
     * @return 해당 채널에 속한 User 객체 리스트 (참여자가 없으면 빈 리스트)
     * @throws IllegalArgumentException 채널이 존재하지 않을 경우 발생
     */
    List<User> findUsersByChannelId(UUID channelId);

    /**
     * 채널-유저 관계 상세 조회
     * 이 유저가 이 채널에서 어떤 권한(Role)을 가지고 있는지 조회 (권한 체크나 정보 수정 시 사용)
     * @param channelId 대상 채널 UUID
     * @param userId    대상 유저 UUID
     * @return 조회된 관계 객체 (ChannelUserRole)
     * @throws IllegalArgumentException 해당 유저가 해당 채널에 참여하고 있지 않을 경우 발생
     */
    ChannelUserRole findChannelUser(UUID channelId, UUID userId);

    /**
     * 특정 유저가 참여 중인 채널 목록 조회 (내가 가입된 채널 목록을 보여줄 때 사용)
     * @param userId 조회할 유저의 UUID
     * @return 유저가 참여 중인 Channel 객체 리스트 (없으면 빈 리스트)
     */
    List<Channel> findChannelsByUserId(UUID userId);

    // Update
    /**
     * 참가자 권한(Role) 수정
     * 특정 채널 내에서 유저의 권한을 변경 (예: 일반 멤버 -> 관리자 승격)
     * @param channelId 대상 채널 UUID
     * @param userId    권한을 변경할 유저 UUID
     * @param newRole   변경할 새로운 권한 (ChannelRole)
     * @return 수정된 관계 객체
     * @throws IllegalArgumentException 참여 정보가 없거나(가입 안 함), 역할이 null일 경우 발생
     */
    ChannelUserRole updateChannelRole(UUID channelId, UUID userId, ChannelRole newRole);

    // Delete
    /**
     * 채널 나가기 / 강퇴 (관계 삭제)
     * 유저와 채널 간의 연결 고리를 끊음
     * @param channelId 나갈 채널의 UUID
     * @param userId    나가는(또는 강퇴당하는) 유저의 UUID
     * @throws IllegalArgumentException 해당 채널에 참여 중이지 않을 경우 발생
     */
    void deleteChannelUser(UUID channelId, UUID userId);

    /**
     * 특정 유저의 모든 채널 참여 정보 삭제
     * 유저가 서비스에서 탈퇴할 때 호출되며, 가입되어 있던 모든 채널에서 나가지게 처리
     * @param userId 탈퇴하는 유저의 UUID
     */
    void deleteAllAssociationsByUserId(UUID userId);
}