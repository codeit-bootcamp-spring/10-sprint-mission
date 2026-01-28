package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;           // 사용자 한 명당 가지는 채널

    private final UserService jcfUserService;

    public JCFChannelService(JCFUserService jcfUserService) {
        this.data = new ArrayList<>();

        this.jcfUserService = jcfUserService;
    }

    // 채널 생성
    @Override
    public Channel createChannel(String channelName, UUID userId, ChannelType channelType) {
        User owner = jcfUserService.searchUser(userId);

        Channel newChannel = new Channel(channelName, owner.getId(), channelType);
        data.add(newChannel);

        return newChannel;
    }

    // 채널 단건 조회
    @Override
    public Channel searchChannel(UUID targetChannelId) {
        return data.stream()
                .filter(channel -> channel.getId().equals(targetChannelId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));
    }

    // 채널 다건 조회
    @Override
    public List<Channel> searchChannelAll() {
        return data;
    }

    // 특정 유저가 참가한 채널 리스트 조회
    @Override
    public List<Channel> searchChannelsByUserId(UUID userId) {
        User targetUser = jcfUserService.searchUser(userId);

        return searchChannelAll().stream()
                .filter(channel -> channel.getMembers().contains(targetUser))
                .toList();
    }

    // 채널 정보 수정
    @Override
    public Channel updateChannel(UUID targetChannelId, String newChannelName) {
        Channel targetChannel = searchChannel(targetChannelId);

        // 채널 이름 변경
        Optional.ofNullable(newChannelName)
                .ifPresent(channelName -> {
                    validateString(channelName, "[채널 이름 변경 실패] 올바른 채널 이름 형식이 아닙니다.");
                    validateDuplicateValue(targetChannel.getChannelName(), channelName, "[채널 이름 변경 실패] 현재 채널 이름과 동일합니다.");
                    targetChannel.updateChannelName(newChannelName);
                });

        return targetChannel;
    }

    @Override
    public void updateChannel(UUID id, Channel channel) {}

    // 채널 삭제
    @Override
    public void deleteChannel(UUID targetChannelId) {
        Channel targetChannel = searchChannel(targetChannelId);

        data.remove(targetChannel);
    }

    // 채널 참가자 초대
    public void inviteMembers(UUID targetUserId, UUID targetChannelId) {
        User newUser = jcfUserService.searchUser(targetUserId);
        Channel targetChannel = searchChannel(targetChannelId);

        validateMemberExists(targetUserId, targetChannelId);

        targetChannel.getMembers().add(newUser.getId());
    }

    // 채널 퇴장
    public void leaveMembers(UUID targetUserId, UUID targetChannelId) {
        User targetUser = jcfUserService.searchUser(targetUserId);
        Channel targetChannel = searchChannel(targetChannelId);

        validateUserNotInChannel(targetUserId, targetChannelId);

        targetChannel.getMembers().remove(targetUser);
    }

    // 유효성 검증 (초대)
    public void validateMemberExists(UUID userId, UUID channelId) {
        List<UUID> currentMembers = jcfUserService.searchMembersByChannelId(channelId);

        if (currentMembers.stream().anyMatch(memberId -> memberId.equals(userId))) {
            throw new IllegalArgumentException("이미 채널에 존재하는 사용자입니다.");
        }
    }

    // 유효성 검증 (퇴장)
    public void validateUserNotInChannel(UUID userId, UUID channelId) {
        List<UUID> currentMembers = jcfUserService.searchMembersByChannelId(channelId);

        if (currentMembers.stream().noneMatch(memberId -> memberId.equals(userId))) {
            throw new IllegalArgumentException("해당 채널에 존재하는 사용자가 아닙니다.");
        }
    }
}