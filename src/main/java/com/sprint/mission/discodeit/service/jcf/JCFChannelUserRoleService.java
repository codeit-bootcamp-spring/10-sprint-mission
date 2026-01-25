package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.*;

import com.sprint.mission.discodeit.service.*;

import java.util.*;
import java.util.stream.Collectors;

public class JCFChannelUserRoleService implements ChannelUserRoleService {
    private final Map<UUID, ChannelUserRole> channelUserRoleDB = new HashMap<>();

    private final UserService userService;
    private final ChannelService channelService;

    public JCFChannelUserRoleService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    // TODO: 채널-유저 관계 생성 -> 채널 생성 이벤트 발생시 자동으로 호출 (-> 이벤트리스너 사용 or Setter 주입)
    @Override
    public ChannelUserRole addChannelUser(UUID channelId, UUID userId, ChannelRole role) {
        Channel channel = channelService.findChannelById(channelId);
        User user = userService.findUserByUserId(userId);

        boolean isAlreadyJoined = channelUserRoleDB.values().stream()
                .anyMatch(cu -> cu.getChannel().getId().equals(channelId)
                        && cu.getUser().getId().equals(userId));
        if (isAlreadyJoined) {
            throw new IllegalArgumentException("이미 채널에 참여 중인 사용자입니다.");
        }

        ChannelUserRole channelUserRole = new ChannelUserRole(channel, user, role);

        channelUserRoleDB.put(channelUserRole.getId(), channelUserRole);
        user.addChannelUserRole(channelUserRole);
        channel.addChannelUserRole(channelUserRole);

        return channelUserRole;
    }

    @Override
    public List<User> findUsersByChannelId(UUID channelId) {
        Channel channel = channelService.findChannelById(channelId);

        return channel.getChannelUserRoles().stream()
                .map(ChannelUserRole::getUser)
                .collect(Collectors.toList());
    }
    @Override
    // TODO: 현재 가장 먼저 찾는 하나의 값만 반환하고 있음 -> 전체 값 List로 반환하도록 수정
    public ChannelUserRole findChannelUser(UUID channelId, UUID userId) {
        return channelUserRoleDB.values().stream()
                .filter(cu -> cu.getChannel().getId().equals(channelId)
                        && cu.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 채널에 참여하지 않은 사용자입니다."
                        + "\nchannelId: " + channelId + "\nuserId: " + userId));
    }
    @Override
    public List<Channel> findChannelsByUserId(UUID userId) {
        return channelUserRoleDB.values().stream()
                .filter(role -> role.getUser().getId().equals(userId))
                .map(ChannelUserRole::getChannel)
                .collect(Collectors.toList());
    }

    @Override
    public ChannelUserRole updateChannelRole(UUID channelId, UUID userId, ChannelRole newRole) {
        ChannelUserRole channelUserRole = findChannelUser(channelId, userId);
        ChannelRole role = findChannelUser(channelId, userId).getChannelRole();

        channelUserRole.updateRole(newRole);
        System.out.println("권한 수정 완료되었습니다.\n"
                + channelUserRole.getUser().getUsername() + ": " + role + " -> " + newRole);
        return channelUserRole;
    }

    @Override
    public void deleteChannelUserAssociation(UUID channelId, UUID userId) {
        ChannelUserRole channelUserRole = findChannelUser(channelId, userId);

        channelUserRole.getChannel().removeChannelUserRole(channelUserRole);
        channelUserRole.getUser().removeChannelUserRole(channelUserRole);
        channelUserRoleDB.remove(channelUserRole.getId());

        System.out.println("채널 탈퇴 완료되었습니다.\n"
                + "channelName: " + channelUserRole.getChannel().getChannelName());
    }
    @Override
    public void deleteAllAssociationsByUserId(UUID userId) {
        List<ChannelUserRole> rolesToDelete = channelUserRoleDB.values().stream()
                .filter(role -> role.getUser().getId().equals(userId))
                .toList();

        // 유저 쪽 삭제
        for (ChannelUserRole role : rolesToDelete) {
            role.getChannel().removeChannelUserRole(role);
        }

        // 매핑테이블 쪽 삭제
        channelUserRoleDB.values().removeAll(rolesToDelete);
        System.out.println("[6] 해당 유저의 채널-유저 관계 삭제 완료되었습니다." +
                "\n\tuserId: " + userId);
    }
    @Override
    public void deleteAllAssociationsByChannelId(UUID channelId) {
        List<ChannelUserRole> rolesToDelete = channelUserRoleDB.values().stream()
                .filter(role -> role.getChannel().getId().equals(channelId))
                .toList();

        // 채널 쪽 삭제
        for (ChannelUserRole role : rolesToDelete) {
            role.getUser().removeChannelUserRole(role);
        }

        // 매핑테이블 쪽 삭제
        channelUserRoleDB.values().removeAll(rolesToDelete);
        System.out.println("\t\t[2] 채널 내 모든 참여자 관계 삭제 완료. (채널 삭제 사전 작업 2)" +
                "\n\t\t\tchannelId: " + channelId);
        for (ChannelUserRole role : rolesToDelete) {
            System.out.println("\t\t\tuserId: " + role.getUser().getId()
                    + "\n\t\t\trole: " + role.getChannelRole() + " 관계 삭제되었습니다.");
        }
    }
}