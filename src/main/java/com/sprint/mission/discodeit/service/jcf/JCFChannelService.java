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
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {
    public static final ArrayList<Channel> channels = new ArrayList<>();        // 사용자 한 명당 가지는 채널
    private final JCFUserService userService = new JCFUserService();

    // 채널 생성
    @Override
    public Channel createChannel(String channelName, UUID userId, ChannelType channelType) {
        User owner = userService.searchUser(userId);

        Channel newChannel = new Channel(channelName, owner, channelType);
        channels.add(newChannel);
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
    public ArrayList<Channel> searchChannelsByUserId(UUID userId) {
        userService.searchUser(userId);

        return channels.stream()
                .filter(channel -> channel.getMembers().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // 특정 채널의 참가자 리스트 조회
    public List<User> searchUsersByChannelId(UUID channelId) {
        searchChannel(channelId);

        Channel targetChannel = channels.stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        return targetChannel.getMembers();
    }

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

        channels.remove(targetChannel);
    }

    // 채널 참가자 초대
    public void inviteMembers(UUID targetUserId, List<User> members) {
        User newUser = userService.searchUser(targetUserId);

        isMemberDuplicated(targetUserId, members);

        members.add(newUser);
    }

    // 채널 퇴장

    // 유효성 검사 (초대)
    public void isMemberDuplicated(UUID targetUserId, List<User> members) {
        if (members.stream().anyMatch(member -> member.getId().equals(targetUserId)))
            throw new IllegalArgumentException("이미 채널에 존재하는 사용자입니다.");
    }
}