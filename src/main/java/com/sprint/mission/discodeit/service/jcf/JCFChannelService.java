package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    public static final ArrayList<Channel> channels = new ArrayList<>();        // 사용자 한 명당 가지는 채널
    private final JCFUserService userService = new JCFUserService();

    // 채널 생성
    @Override
    public Channel createChannel(String channelName, UUID userId, ChannelType channelType) {
        User owner = userService.searchUser(userId);

        Channel newChannel = new Channel(channelName, owner, channelType);
        channels.add(newChannel);
        owner.addChannel(newChannel);

        return newChannel;
    }

    // 채널 단건 조회
    @Override
    public Channel searchChannel(UUID targetChannelId) {
        return channels.stream()
                .filter(channel -> channel.getId().equals(targetChannelId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));
    }

    // 채널 다건 조회
    @Override
    public ArrayList<Channel> searchChannelAll() {
        return channels;
    }

    // 특정 유저가 참가한 채널 리스트 조회
    public List<Channel> searchChannelsByUserId(UUID userId) {
        User targetUser = userService.searchUser(userId);

        return targetUser.getChannels();
    }

    // 채널 정보 수정
    @Override
    public Channel updateChannel(UUID targetChannelId, String newChannelName) {
        Channel targetChannel = searchChannel(targetChannelId);

        Optional.ofNullable(newChannelName)
                .ifPresent(channelName -> {
                    validateString(channelName, "[채널 이름 변경 실패] 올바른 채널 이름 형식이 아닙니다.");
                    validateDuplicateValue(targetChannel.getChannelName(), channelName, "[채널 이름 변경 실패] 현재 채널 이름과 동일합니다.");
                    targetChannel.updateChannelName(newChannelName);
                });

        return targetChannel;
    }

    // 채널 삭제
    @Override
    public void deleteChannel(UUID targetChannelId) {
        Channel targetChannel = searchChannel(targetChannelId);

        targetChannel.getMembers().forEach(member -> {member.removeChannel(targetChannel);});

        channels.remove(targetChannel);
    }

    // 채널 참가자 초대
    public void inviteMembers(UUID targetUserId, UUID targetChannelId) {
        User newUser = userService.searchUser(targetUserId);
        Channel targetChannel = searchChannel(targetChannelId);

        validateMemberExists(targetUserId, targetChannelId);

        targetChannel.getMembers().add(newUser);
        newUser.addChannel(targetChannel);
    }

    // 채널 퇴장
    public void leaveMembers(UUID targetUserId, UUID targetChannelId) {
        User targetUser = userService.searchUser(targetUserId);
        Channel targetChannel = searchChannel(targetChannelId);

        validateUserNotInChannel(targetUserId, targetChannelId);

        targetChannel.getMembers().remove(targetUser);
        targetUser.removeChannel(targetChannel);
    }

    // 유효성 검증 (초대)
    public void validateMemberExists(UUID userId, UUID channelId) {
        List<User> currentMembers = userService.searchUsersByChannelId(channelId);

        if (currentMembers.stream().anyMatch(member -> member.getId().equals(userId))) {
            throw new IllegalArgumentException("이미 채널에 존재하는 사용자입니다.");
        }
    }

    // 유효성 검증 (퇴장)
    public void validateUserNotInChannel(UUID userId, UUID channelId) {
        List<User> currentMembers = userService.searchUsersByChannelId(channelId);

        if (currentMembers.stream().noneMatch(member -> member.getId().equals(userId))) {
            throw new IllegalArgumentException("해당 채널에 존재하는 사용자가 아닙니다.");
        }
    }
}