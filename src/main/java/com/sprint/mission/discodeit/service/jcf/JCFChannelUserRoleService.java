package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelUserRole;
import com.sprint.mission.discodeit.entity.ChannelRole;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ChannelUserRoleService;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFChannelUserRoleService implements ChannelUserRoleService {
    private final Map<UUID, ChannelUserRole> channelUserMap = new HashMap<>();
    // 의존성
    private final UserService userService;
    private final ChannelService channelService;

    // 생성자 주입
    public JCFChannelUserRoleService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    // Create
    @Override
    public ChannelUserRole addChannelUser(UUID channelId, UUID userId, ChannelRole role) {
        // 1 유효성 검사 (존재하는 채널/유저인지 확인)
        Channel channel = channelService.findChannelById(channelId);
        User user = userService.findUserByUserId(userId);

        // 2 채널 중복 참여 검사
        boolean isAlreadyJoined = channelUserMap.values().stream()
                .anyMatch(cu -> cu.getChannel().getId().equals(channelId) &&
                        cu.getUser().getId().equals(userId));
        if (isAlreadyJoined) {
            throw new IllegalArgumentException("이미 채널에 참여 중인 사용자");
        }

        // 3 ChannelUser 생성 및 저장
        ChannelUserRole channelUserRole = new ChannelUserRole(channel, user, role);

        // 4 데이터 정합성 맞추기 (양쪽 다 저장)
        channelUserMap.put(channelUserRole.getId(), channelUserRole);  // (1) Map에 등록
        user.addChannelUserRole(channelUserRole);  // (2) List에도 등록
        channel.addChannelUserRole(channelUserRole); // (3) Channel 리스트에도 등록

        return channelUserRole;
    }

    // Read
    // 특정 채널의 모든 유저 조회
    @Override
    public List<User> findUsersByChannelId(UUID channelId) {
        Channel channel = channelService.findChannelById(channelId);

        // 채널 객체 내 리스트 활용
        return channel.getChannelUserRoles().stream()
                .map(ChannelUserRole::getUser)
                .collect(Collectors.toList());
    }
    @Override
    // 특정 채널에 특정 유저 조회
    public ChannelUserRole findChannelUser(UUID channelId, UUID userId) {
        // (채널ID + 유저ID)가 모두 일치하는 ChannelUser 객체 찾기
        return channelUserMap.values().stream()
                .filter(cu -> cu.getChannel().getId().equals(channelId) &&
                        cu.getUser().getId().equals(userId))
                .findFirst() // 찾으면 Optional 반환
                .orElseThrow(() -> new IllegalArgumentException("해당 채널에 참여하지 않은 사용자입니다."));
    }
    // 특정 유저가 참가하고 있는 채널 리스트 반환
    @Override
    public List<Channel> findChannelsByUserId(UUID userId) {
        return channelUserMap.values().stream()
                .filter(role -> role.getUser().getId().equals(userId))
                .map(ChannelUserRole::getChannel)
                .collect(Collectors.toList());
    }

    // Update
    @Override
    public ChannelUserRole updateChannelRole(UUID channelId, UUID userId, ChannelRole newRole) {
        // 1. 수정할 대상을 찾음 (위에서 만든 Read 메서드 재사용)
        ChannelUserRole channelUserRole = findChannelUser(channelId, userId);

        // 2. 데이터 수정 (ChannelUser 엔티티 내부에 updateRole 메서드가 있어야 함)
        channelUserRole.updateRole(newRole);

        System.out.println("권한 수정 완료: " + channelUserRole.getUser().getUsername() + " -> " + newRole);
        return channelUserRole;
    }

    // Delete
    @Override
    public void deleteChannelUser(UUID channelId, UUID userId) {
        // 1. 삭제할 대상을 찾음
        ChannelUserRole channelUserRole = findChannelUser(channelId, userId);
        channelUserRole.getChannel().removeChannelUserRole(channelUserRole);

        channelUserMap.remove(channelUserRole.getId());  // Map에서 제거
        User user = channelUserRole.getUser();  // 유저 객체 꺼내고
        user.removeChannelUserRole(channelUserRole);  // 그 유저에게 삭제 명령

        System.out.println("채널 탈퇴 완료: " + channelUserRole.getUser().getUsername()
                + " (채널: " + channelUserRole.getChannel().getChannelName() + ")");
    }
    @Override
    public void deleteAllAssociationsByUserId(UUID userId) {
        // 1 이 유저와 관련된 모든 관계(Role)를 먼저 찾음
        List<ChannelUserRole> rolesToDelete = channelUserMap.values().stream()
                .filter(role -> role.getUser().getId().equals(userId))
                .toList(); // Java 16+

        // 2 각 관계가 속해있던 "채널"의 명부에서 이 유저를 지움
        for (ChannelUserRole role : rolesToDelete) {
            // 채널 객체를 가져와서 해당 Role 삭제
            role.getChannel().removeChannelUserRole(role);
        }

        // 3 Map에서 일괄 삭제
        channelUserMap.values().removeAll(rolesToDelete);
    }
    @Override
    public void deleteAllAssociationsByChannelId(UUID channelId) {
        // 1. 해당 채널에 속한 모든 관계(Role)를 찾음
        List<ChannelUserRole> rolesToDelete = channelUserMap.values().stream()
                .filter(role -> role.getChannel().getId().equals(channelId))
                .toList(); // Java 16+ (그 이하면 .collect(Collectors.toList()))

        // 2. 각 유저 객체의 리스트에서도 관계 정보를 제거
        for (ChannelUserRole role : rolesToDelete) {
            role.getUser().removeChannelUserRole(role);
        }

        // 3. 맵에서 일괄 삭제
        channelUserMap.values().removeAll(rolesToDelete);
        System.out.println("[2] 채널 내 모든 참여자 관계 삭제 완료. channelId: " + channelId);
    }

}