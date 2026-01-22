package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatusType;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateString;

public class FileUserService implements UserService {           // 경로 설정
    private FileUserRepository fileUserRepository;

    private FileChannelService fileChannelService;
    private FileMessageService fileMessageService;

    public FileUserService(FileUserRepository fileUserRepository) {
        this.fileUserRepository = fileUserRepository;
    }

    // 사용자 생성
    @Override
    public User createUser(String email, String password, String nickname, UserStatusType userStatus) {
        isEmailDuplicate(email);
        User newUser = new User(email, password, nickname, userStatus);

        fileUserRepository.save(newUser);
        return newUser;
    }

    // 사용자 단건 조회
    @Override
    public User searchUser(UUID userId) {
        return fileUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
    }

    // 사용자 전체 조회
    @Override
    public List<User> searchUserAll() {
        return fileUserRepository.findAll();
    }

    // 특정 채널의 참가자 목록 조회
    public List<User> searchMembersByChannelId(UUID channelId) {
        Channel targetChannel = fileChannelService.searchChannel(channelId);      // 함수가 실행된 시점에서의 가장 최신 채널 목록

        return targetChannel.getMembers();
    }

    // 사용자 정보 수정
    @Override
    public User updateUser(UUID userId, String newPassword, String newNickname, UserStatusType newUserStatus) {
        User targetUser = searchUser(userId);

        Optional.ofNullable(newPassword)        // 비밀번호 필드 변경
                .ifPresent(password -> {
                    validateString(password, "[비밀 번호 변경 실패] 올바른 비밀 번호 형식이 아닙니다.");
                    validateDuplicateValue(targetUser.getPassword(), newPassword, "[비밀 번호 변경 실패] 현재 비밀 번호와 일치합니다.");
                    targetUser.updatePassword(password);
                });

        Optional.ofNullable(newNickname)        // 닉네임 필드 변경
                .ifPresent(nickname -> {
                    validateString(nickname, "[닉네임 변경 실패] 올바른 닉네임 형식이 아닙니다.");
                    validateDuplicateValue(targetUser.getNickname(), newNickname, "[닉네임 변경 실패] 현재 닉네임과 일치합니다.");
                    targetUser.updateNickname(nickname);
                });

        Optional.ofNullable(newUserStatus)      //  상태 필드 변경
                .ifPresent(targetUser::updateUserStatus);

        fileUserRepository.save(targetUser);
        return targetUser;
    }

    // 파일 내 사용자 수정 (덮어쓰기)
    public void updateUser(User targetUser) {
        fileUserRepository.save(targetUser);
    }

    // 사용자 삭제
    @Override
    public void deleteUser(UUID userId) {
        User targetUser = searchUser(userId);

        targetUser.getChannels()                                 // 모든 채널의 member에서 해당 유저를 제거
                .forEach(channel -> {
                    Channel realChannel = fileChannelService.searchChannel(channel.getId());
                    realChannel.getMembers().removeIf(member -> member.getId().equals(userId));
                    fileChannelService.updateChannel(realChannel);
                });

        fileMessageService.searchMessageAll().stream()          // 모든 메시지에서 해당 유저가 작성한 메시지 제거
                .filter(message -> message.getUser().getId().equals(userId))
                .forEach(message -> fileMessageService.deleteMessage(message.getId()));

        fileUserRepository.delete(targetUser);
    }

    // 유효성 검사 (이메일 중복)
    public void isEmailDuplicate(String email) {
        if (fileUserRepository.existsByEmail(email))
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