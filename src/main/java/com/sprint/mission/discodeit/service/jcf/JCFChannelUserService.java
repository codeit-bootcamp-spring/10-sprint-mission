package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelUserRole;
import com.sprint.mission.discodeit.entity.ChannelRole;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ChannelUserService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFChannelUserService implements ChannelUserService {
    // DB (Repository 계층 대체)
    private final Map<UUID, ChannelUserRole> channelUserMap = new HashMap<>();

    // 의존성 주입
    private final UserService userService;
    private final ChannelService channelService;

    public JCFChannelUserService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    // Create
    @Override
    public ChannelUserRole addChannelUser(UUID channelId, UUID userId, ChannelRole role) {
        // 1. 유효성 검사 (존재하는 채널/유저인지 확인)
        Channel channel = channelService.findChannelById(channelId);
        User user = userService.findUserByUserId(userId);

        // 2. 중복 참여 검사
        boolean isAlreadyJoined = channelUserMap.values().stream()
                .anyMatch(cu -> cu.getChannel().getId().equals(channelId) &&
                        cu.getUser().getId().equals(userId));
        if (isAlreadyJoined) {
            throw new IllegalArgumentException("이미 채널에 참여 중인 사용자");
        }

        // 3. ChannelUser 생성 및 저장
        ChannelUserRole channelUserRole = new ChannelUserRole(channel, user, role);
        channelUserMap.put(channelUserRole.getId(), channelUserRole);

        System.out.println("채널 참여 완료: " + user.getUsername() + " -> " + channel.getChannelName()
                + " (Role: " + role + ")");
        return channelUserRole;
    }

    // Read
    @Override
    public List<User> findUsersByChannelId(UUID channelId) {
        // 채널 존재 확인
        channelService.findChannelById(channelId);

        // 해당 채널ID를 가진 ChannelUser들을 찾아서 -> 그 안의 User 객체만 추출하여 리스트로 반환
        return channelUserMap.values().stream()
                .filter(cu -> cu.getChannel().getId().equals(channelId))
                .map(ChannelUserRole::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public ChannelUserRole findChannelUser(UUID channelId, UUID userId) {
        // 스트림을 사용하여 (채널ID + 유저ID)가 모두 일치하는 ChannelUser 객체 찾기
        return channelUserMap.values().stream()
                .filter(cu -> cu.getChannel().getId().equals(channelId) &&
                        cu.getUser().getId().equals(userId))
                .findFirst() // 찾으면 Optional 반환
                .orElseThrow(() -> new IllegalArgumentException("해당 채널에 참여하지 않은 사용자입니다."));
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

        // 2. Map에서 제거
        channelUserMap.remove(channelUserRole.getId());

        System.out.println("채널 탈퇴 완료: " + channelUserRole.getUser().getUsername()
                + " (채널: " + channelUserRole.getChannel().getChannelName() + ")");
    }
}