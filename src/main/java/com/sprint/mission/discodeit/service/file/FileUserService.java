package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatusType;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
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

public class FileUserService implements UserService {
    private final Path directory = Paths.get(System.getProperty("user.dir"), "data", "users");              // 경로 설정
    private ChannelService fileChannelService;
    private MessageService fileMessageService;

    public FileUserService() {
        FileUtil.init(directory);
    }

    // 사용자 생성
    @Override
    public User createUser(String email, String password, String nickname, UserStatusType userStatus) {
        isEmailDuplicate(email);
        User newUser = new User(email, password, nickname, userStatus);

        Path filePath = directory.resolve(newUser.getId() + ".ser");
        FileUtil.save(filePath, newUser);
        return newUser;
    }

    // 사용자 단건 조회
    @Override
    public User searchUser(UUID userId) {
        return FileUtil.loadSingle(directory.resolve(userId + ".ser"));
    }

    // 사용자 전체 조회
    @Override
    public List<User> searchUserAll() {
        return FileUtil.load(directory);
    }

    // 특정 채널의 참가자 리스트 조회
    public List<User> searchMembersByChannelId(UUID channelId) {
        Channel channel = fileChannelService.searchChannel(channelId);      // 함수가 실행된 시점에서의 가장 최신 채널 목록

        return channel.getMembers();
    }

    // 사용자 정보 수정
    @Override
    public User updateUser(UUID userId, String newPassword, String newNickname, UserStatusType newUserStatus) {
        User targetUser = searchUser(userId);

        // 비밀번호 필드 변경
        Optional.ofNullable(newPassword)
                .ifPresent(password -> {
                    validateString(password, "[비밀 번호 변경 실패] 올바른 비밀 번호 형식이 아닙니다.");
                    validateDuplicateValue(targetUser.getPassword(), newPassword, "[비밀 번호 변경 실패] 현재 비밀 번호와 일치합니다.");
                    targetUser.updatePassword(password);
                });

        // 닉네임 필드 변경
        Optional.ofNullable(newNickname)
                .ifPresent(nickname -> {
                    validateString(nickname, "[닉네임 변경 실패] 올바른 닉네임 형식이 아닙니다.");
                    validateDuplicateValue(targetUser.getNickname(), newNickname, "[닉네임 변경 실패] 현재 닉네임과 일치합니다.");
                    targetUser.updateNickname(nickname);
                });

        // 상태 필드 변경
        Optional.ofNullable(newUserStatus)
                .ifPresent(targetUser::updateUserStatus);

        FileUtil.save(directory.resolve(userId + ".ser"), targetUser);
        return targetUser;
    }

    // 파일 내 사용자 수정 (덮어쓰기)
    @Override
    public void updateUser(UUID targetUserId, User user) {
        FileUtil.save(directory.resolve(targetUserId + ".ser"), user);
    }

    // 사용자 삭제
    @Override
    public void deleteUser(UUID userId) {
        searchUser(userId);

        // 모든 채널의 member에서 해당 유저를 제거
        fileChannelService.searchChannelAll().stream()
                .filter(channel -> channel.getMembers().stream().anyMatch(member -> member.getId().equals(userId)))
                .forEach(channel -> {
                    channel.getMembers().removeIf(member -> member.getId().equals(userId));
                    fileChannelService.updateChannel(channel.getId(), channel);
                });

        // 모든 메시지에서 해당 유저가 작성한 메시지 제거
        fileMessageService.searchMessageAll().stream()
                .filter(message -> message.getUser().getId().equals(userId))
                .forEach(message -> fileMessageService.deleteMessage(message.getId()));

        try {
            Files.deleteIfExists(directory.resolve(userId + ".ser"));
        } catch (IOException e) {
            throw new RuntimeException("[삭제 실패] 시스템 오류가 발생했습니다.", e);
        }
    }

    // 유효성 검사 (이메일 중복)
    public void isEmailDuplicate(String email) {
        if (searchUserAll().stream().anyMatch(user -> user.getEmail().equals(email)))
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    // setter
    public void setFileChannelService(FileChannelService fileChannelService) {
        this.fileChannelService = fileChannelService;
    }

    public void setFileMessageService(FileMessageService fileMessageService) {
        this.fileMessageService = fileMessageService;
    }
}