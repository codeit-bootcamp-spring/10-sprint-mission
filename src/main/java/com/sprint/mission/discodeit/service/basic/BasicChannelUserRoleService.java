package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelRole;
import com.sprint.mission.discodeit.entity.ChannelUserRole;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ChannelUserRoleRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelUserRoleService;

import java.util.*;
import java.util.stream.Collectors;

public class BasicChannelUserRoleService implements ChannelUserRoleService {
    private final ChannelUserRoleRepository channelUserRoleRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public BasicChannelUserRoleService(ChannelUserRoleRepository channelUserRoleRepository,
                                       UserRepository userRepository,
                                       ChannelRepository channelRepository) {
        this.channelUserRoleRepository = channelUserRoleRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public ChannelUserRole addChannelUser(UUID channelId, UUID userId, ChannelRole role) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        if (channelUserRoleRepository.existsByChannelIdAndUserId(channelId, userId)) {
            throw new IllegalArgumentException("이미 채널에 참여 중인 사용자입니다.");
        }

        ChannelUserRole channelUserRole = new ChannelUserRole(channel, user, role);

        // (선택 사항) 메모리 객체 그래프 일관성 유지
        user.addChannelUserRole(channelUserRole);
        channel.addChannelUserRole(channelUserRole);

        return channelUserRoleRepository.save(channelUserRole);
    }

    @Override
    public List<User> findUsersByChannelId(UUID channelId) {
        if (channelRepository.findById(channelId).isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        return channelUserRoleRepository.findAllByChannelId(channelId).stream()
                .map(ChannelUserRole::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public ChannelUserRole findChannelUser(UUID channelId, UUID userId) {
        return channelUserRoleRepository.findByChannelIdAndUserId(channelId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널에 참여하지 않은 사용자입니다."));
    }

    @Override
    public List<Channel> findChannelsByUserId(UUID userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
        return channelUserRoleRepository.findAllByUserId(userId).stream()
                .map(ChannelUserRole::getChannel)
                .collect(Collectors.toList());
    }

    @Override
    public ChannelUserRole updateChannelRole(UUID channelId, UUID userId, ChannelRole newRole) {
        ChannelUserRole role = findChannelUser(channelId, userId);
        role.updateRole(newRole);
        return channelUserRoleRepository.save(role);
    }

    @Override
    public void deleteChannelUserAssociation(UUID channelId, UUID userId) {
        ChannelUserRole role = findChannelUser(channelId, userId);

        // 메모리 객체 정리 (선택 사항)
        try {
            role.getUser().removeChannelUserRole(role);
            role.getChannel().removeChannelUserRole(role);
        } catch (Exception e) { /* 무시 */ }

        channelUserRoleRepository.delete(role.getId());
        System.out.println("채널 탈퇴 완료 (Basic).");
    }

    @Override
    public void deleteAllAssociationsByUserId(UUID userId) {
        channelUserRoleRepository.deleteAllByUserId(userId);
    }

    @Override
    public void deleteAllAssociationsByChannelId(UUID channelId) {
        channelUserRoleRepository.deleteAllByChannelId(channelId);
    }
}