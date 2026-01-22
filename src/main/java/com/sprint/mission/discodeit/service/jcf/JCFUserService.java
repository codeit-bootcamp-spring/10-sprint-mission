package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatusType;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.*;

public class JCFUserService implements UserService {
    private final List<User> data;             // 전체 사용자

    private JCFChannelService jcfChannelService;
    private JCFMessageService jcfMessageService;

    public JCFUserService() {
        this.data = new ArrayList<>();
    };

    // 사용자 생성
    @Override
    public User createUser(String email, String password, String nickname, UserStatusType userStatus) {
        isEmailDuplicate(email);
        User newUser = new User(email, password, nickname, userStatus);
        data.add(newUser);
        return newUser;
    }

    // 사용자 단건 조회
    @Override
    public User searchUser(UUID targetUserId) {
        return data.stream()
                .filter(user -> user.getId().equals(targetUserId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
    }

    // 사용자 전체 조회
    @Override
    public List<User> searchUserAll() {
        return data;
    }

    // 특정 채널의 참가자 리스트 조회
    public List<User> searchMembersByChannelId(UUID channelId) {
        Channel targetChannel = jcfChannelService.searchChannelAll().stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        return targetChannel.getMembers();
    }

    // 사용자 정보 수정 (비밀번호, 닉네임) - 유연하게 계선
    @Override
    public User updateUser(UUID targetUserId, String newPassword, String newNickname, UserStatusType newUserStatus) {
        User targetUser = searchUser(targetUserId);

        // 비밀번호 변경
        Optional.ofNullable(newPassword)
                .ifPresent(password -> {
                    validateString(password, "[비밀 번호 변경 실패] 올바른 비밀 번호 형식이 아닙니다.");
                    validateDuplicateValue(targetUser.getPassword(), newPassword, "[비밀 번호 변경 실패] 현재 비밀 번호와 일치합니다.");
                    targetUser.updatePassword(password);
                });

        // 닉네임 변경
        Optional.ofNullable(newNickname)
                .ifPresent(nickname -> {
                    validateString(nickname, "[닉네임 변경 실패] 올바른 닉네임 형식이 아닙니다.");
                    validateDuplicateValue(targetUser.getNickname(), newNickname, "[닉네임 변경 실패] 현재 닉네임과 일치합니다.");
                    targetUser.updateNickname(nickname);
                });

        // 상태 변경
        Optional.ofNullable(newUserStatus)
                .ifPresent(targetUser::updateUserStatus);

        return targetUser;
    }

    // 사용자 삭제
    @Override
    public void deleteUser(UUID targetUserId) {
        User targetUser = searchUser(targetUserId);

        // 모든 채널의 member에서 해당 유저를 제거
        jcfChannelService.searchChannelAll().stream()
                .filter(channel -> targetUser.getChannels().contains(channel))
                .forEach(channel -> channel.getMembers().remove(targetUser));

        // 모든 메시지에서 해당 유저가 작성한 메시지 제거
        jcfMessageService.searchMessageAll()
                .removeIf(message -> targetUser.getMessages().contains(message));

        data.remove(targetUser);
    }

    // 유효성 검사 (생성)
    public void isEmailDuplicate(String email) {
        if (data.stream().anyMatch(user -> user.getEmail().equals(email)))
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    public void setJCFChannelService(JCFChannelService jcfChannelService) {
        this.jcfChannelService = jcfChannelService;
    }

    public void setJCFMessageService(JCFMessageService jcfMessageService) {
        this.jcfMessageService = jcfMessageService;
    }
}