package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.util.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateString;

public class FileChannelService implements ChannelService {
    private final Path directory = Paths.get(System.getProperty("user.dir"), "data", "channels");       // 경로 설정
    private final UserService fileUserService;

    public FileChannelService(FileUserService fileUserService) {
        FileUtil.init(directory);

        this.fileUserService = fileUserService;
    }

    // 채널 생성
    @Override
    public Channel createChannel(String channelName, UUID userId, ChannelType channelType) {
        User owner = fileUserService.searchUser(userId);

        Channel newChannel = new Channel(channelName, owner, channelType);
        Path filePath = directory.resolve(newChannel.getId() + ".ser");
        FileUtil.save(filePath, newChannel);

        owner.addChannel(newChannel);
        fileUserService.updateUser(owner.getId(), owner);

        return newChannel;
    }

    // 채널 단건 조회
    @Override
    public Channel searchChannel(UUID targetChannelId) {
        return FileUtil.loadSingle(directory.resolve(targetChannelId + ".ser"));
    }

    // 채널 전체 조회
    @Override
    public List<Channel> searchChannelAll() {
        return FileUtil.load(directory);
    }

    // 특정 사용자가 참가한 채널 목록 조회
    public List<Channel> searchChannelsByUserId(UUID userId) {
        fileUserService.searchUser(userId);

        List<Channel> channels = searchChannelAll();        // 함수가 실행된 시점에서 가장 최신 채널 목록

        return channels.stream()
                .filter(channel -> channel.getMembers().stream()
                        .anyMatch(member -> member.getId().equals(userId)))
                .toList();
    }

    // 채널 정보 수정
    @Override
    public Channel updateChannel(UUID targetChannelId, String newChannelName) {
        Channel targetChannel = searchChannel(targetChannelId);

        // 채널 이름 필드 변경
        Optional.ofNullable(newChannelName)
                .ifPresent(channelName -> {
                    validateString(channelName, "[채널 이름 변경 실패] 올바른 채널 이름 형식이 아닙니다.");
                    validateDuplicateValue(targetChannel.getChannelName(), channelName, "[채널 이름 변경 실패] 현재 채널 이름과 동일합니다.");
                    targetChannel.updateChannelName(newChannelName);
                });

        FileUtil.save(directory.resolve(targetChannelId + ".ser"), targetChannel);
        return targetChannel;
    }

    // 파일 내 채널 수정 (덮어쓰기)
    public void updateChannel (UUID targetChannelId, Channel targetChannel) {
        FileUtil.save(directory.resolve(targetChannelId + ".ser"), targetChannel);
    }

    // 채널 삭제
    @Override
    public void deleteChannel(UUID targetChannelId) {
        Channel targetChannel = searchChannel(targetChannelId);

        // 모든 채널 멤버에서 채널 목록 삭제
        targetChannel.getMembers().forEach(member -> {
            User targetUser = fileUserService.searchUser(member.getId());
            targetUser.getChannels().removeIf(channel -> channel.getId().equals(targetChannelId));
            fileUserService.updateUser(targetUser.getId(), targetUser);
        });

        try {
            Files.deleteIfExists(directory.resolve(targetChannelId + ".ser"));
        } catch (IOException e) {
            throw new RuntimeException("[삭제 실패] 시스템 오류가 발생했습니다.", e);
        }
    }

    // 채널 참가자 초대
    public void inviteMembers(UUID targetUserId, UUID targetChannelId) {
        User newUser = fileUserService.searchUser(targetUserId);
        Channel targetChannel = searchChannel(targetChannelId);

        validateMemberExists(targetUserId, targetChannelId);

        newUser.addChannel(targetChannel);
        fileUserService.updateUser(newUser.getId(), newUser);

        targetChannel.getMembers().add(newUser);
        FileUtil.save(directory.resolve(targetChannelId + ".ser"), targetChannel);
    }

    // 채널 퇴장
    public void leaveMembers(UUID targetUserId, UUID targetChannelId) {
        User targetUser = fileUserService.searchUser(targetUserId);
        Channel targetChannel = searchChannel(targetChannelId);

        validateUserNotInChannel(targetUserId, targetChannelId);

        targetChannel.getMembers().removeIf(member -> member.getId().equals(targetUser.getId()));
        FileUtil.save(directory.resolve(targetChannelId + ".ser"), targetChannel);

        targetUser.getChannels().removeIf(channel -> channel.getId().equals(targetUser.getId()));
        fileUserService.updateUser(targetUser.getId(), targetUser);
    }

    // 유효성 검증 (초대)
    public void validateMemberExists(UUID userId, UUID channelId) {
        List<User> currentMembers = fileUserService.searchMembersByChannelId(channelId);

        boolean isExist = currentMembers.stream()
                .anyMatch(member -> member.getId().equals(userId));

        if (isExist) {
            throw new IllegalArgumentException("이미 채널에 존재하는 사용자입니다.");
        }
    }

    // 유효성 검증 (퇴장)
    public void validateUserNotInChannel(UUID userId, UUID channelId) {
        List<User> currentMembers = fileUserService.searchMembersByChannelId(channelId);

        boolean notInChannel = currentMembers.stream()
                .noneMatch(member -> member.getId().equals(userId));

        if (notInChannel) {
            throw new IllegalArgumentException("해당 채널에 존재하는 사용자가 아닙니다.");
        }
    }
}