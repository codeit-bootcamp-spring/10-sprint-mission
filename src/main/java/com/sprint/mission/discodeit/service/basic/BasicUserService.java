package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatusType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateString;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public BasicUserService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    // 사용자 생성
    @Override
    public User createUser(String email, String password, String nickname, UserStatusType userStatus) {
        isEmailDuplicate(email);
        User newUser = new User(email, password, nickname, userStatus);

        userRepository.save(newUser);
        return newUser;
    }

    // 사용자 단건 조회
    @Override
    public User searchUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
    }

    // 사용자 전체 조회
    @Override
    public List<User> searchUserAll() {
        return userRepository.findAll();
    }

    // 특정 채널의 참가자 목록 조회
    public List<User> searchMembersByChannelId(UUID channelId) {
        channelRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        return channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."))
                .getMembers();
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

        userRepository.save(targetUser);
        return targetUser;
    }

    // 사용자 삭제
    @Override
    public void deleteUser(UUID userId) {
        User targetUser = searchUser(userId);

        targetUser.getChannels()                                 // 모든 채널의 member에서 해당 유저를 제거
                .forEach(channel -> {
                    Channel realChannel = channelRepository.findById(channel.getId())
                            .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));
                    realChannel.getMembers().removeIf(member -> member.getId().equals(userId));
                    channelRepository.save(realChannel);
                });

        messageRepository.findAll().stream()          // 모든 메시지에서 해당 유저가 작성한 메시지 제거
                .filter(message -> message != null && message.getUser() != null)
                .filter(message -> message.getUser().getId().equals(userId))
                .forEach(messageRepository::delete);

        userRepository.delete(targetUser);
    }

    // 유효성 검사 (이메일 중복)
    public void isEmailDuplicate(String email) {
        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }
}
