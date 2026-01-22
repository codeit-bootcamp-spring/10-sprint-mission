package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final JCFChannelRepository jcfChannelRepository;
    private final JCFUserService jcfUserService;

    public JCFChannelService(JCFChannelRepository jcfChannelRepository, JCFUserService jcfUserService) {
        this.jcfChannelRepository = jcfChannelRepository;
        this.jcfUserService = jcfUserService;
    }

    // 채널 생성
    @Override
    public Channel createChannel(String channelName, UUID userId, ChannelType channelType) {
        User owner = jcfUserService.searchUser(userId);

        Channel newChannel = new Channel(channelName, owner, channelType);
        jcfChannelRepository.save(newChannel);
        owner.addChannel(newChannel);

        return newChannel;
    }

    // 채널 단건 조회
    @Override
    public Channel searchChannel(UUID targetChannelId) {
        return jcfChannelRepository.findById(targetChannelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));
    }

    // 채널 다건 조회
    @Override
    public List<Channel> searchChannelAll() {
        return jcfChannelRepository.findAll();
    }

    // 특정 유저가 참가한 채널 리스트 조회
    public List<Channel> searchChannelsByUserId(UUID userId) {
        User targetUser = jcfUserService.searchUser(userId);

        return targetUser.getChannels();
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

    // 채널 삭제
    @Override
    public void deleteChannel(UUID targetChannelId) {
        Channel targetChannel = searchChannel(targetChannelId);

        targetChannel.getMembers()      // 채널 내 모든 멤버의 채널 목록에서 채널 삭제
                .forEach(member -> member.removeChannel(targetChannel));

        jcfChannelRepository.delete(targetChannel);
    }

    // 채널 참가자 초대
    public void inviteMembers(UUID targetUserId, UUID targetChannelId) {
        User newUser = jcfUserService.searchUser(targetUserId);
        Channel targetChannel = searchChannel(targetChannelId);

        validateMemberExists(targetUserId, targetChannelId);

        targetChannel.getMembers().add(newUser);
        newUser.addChannel(targetChannel);
    }

    // 채널 퇴장
    public void leaveMembers(UUID targetUserId, UUID targetChannelId) {
        User targetUser = jcfUserService.searchUser(targetUserId);
        Channel targetChannel = searchChannel(targetChannelId);

        validateUserNotInChannel(targetUserId, targetChannelId);

        targetChannel.getMembers().remove(targetUser);
        targetUser.removeChannel(targetChannel);
    }

    // 유효성 검증 (초대)
    public void validateMemberExists(UUID userId, UUID channelId) {
        List<User> currentMembers = jcfUserService.searchMembersByChannelId(channelId);

        if (currentMembers.stream().anyMatch(member -> member.getId().equals(userId))) {
            throw new IllegalArgumentException("이미 채널에 존재하는 사용자입니다.");
        }
    }

    // 유효성 검증 (퇴장)
    public void validateUserNotInChannel(UUID userId, UUID channelId) {
        List<User> currentMembers = jcfUserService.searchMembersByChannelId(channelId);

        if (currentMembers.stream().noneMatch(member -> member.getId().equals(userId))) {
            throw new IllegalArgumentException("해당 채널에 존재하는 사용자가 아닙니다.");
        }
    }
}