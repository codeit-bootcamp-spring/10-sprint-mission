package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatusType;
import com.sprint.mission.discodeit.service.UserService;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.*;

public class JCFUserService implements UserService {
    public static final ArrayList<User> users = new ArrayList<>();         // 전체 사용

    // 사용자 생성
    @Override
    public User createUser(String email, String password, String nickname, UserStatusType userStatus) {
        if (isEmailDuplicate(email)){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User newUser = new User(email, password, nickname, userStatus);
        users.add(newUser);
        return newUser;
    }

    // 사용자 단건 조회
    @Override
    public User searchUser(UUID targetUserId) {
        return users.stream()
                    .filter(user -> user.getId().equals(targetUserId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
    }

    // 사용자 전체 조회
    @Override
    public ArrayList<User> searchUserAll() {
        return users;
    }

    // 사용자 정보 수정 (비밀번호, 닉네임) - 유연하게 계선
    @Override
    public void updateUser(UUID targetUserId, String newPassword, String newNickname, UserStatusType newUserStatus) {
        User targetUser = searchUser(targetUserId);

        Optional.ofNullable(newPassword)
                .ifPresent(password -> {
                            validateString(password, "[비밀 번호 변경 실패] 올바른 비밀 번호 형식이 아닙니다.");
                            validateDuplicateValue(targetUser.getPassword(), newPassword, "[비밀 번호 변경 실패] 현재 비밀 번호와 일치합니다.");
                            targetUser.updatePassword(password);
                        });

        Optional.ofNullable(newNickname)
                .ifPresent(nickname -> {
                            validateString(nickname, "[닉네임 변경 실패] 올바른 닉네임 형식이 아닙니다.");
                            validateDuplicateValue(targetUser.getNickname(), newNickname, "[닉네임 변경 실패] 현재 닉네임과 일치합니다.");
                            targetUser.updateNickname(nickname);
                });

        Optional.ofNullable(newUserStatus)
                .ifPresent(targetUser::updateUserStatus);
    }

    // 사용자 삭제
    @Override
    public void deleteUser(UUID targetUserId) {
        User targetUser = searchUser(targetUserId);

        users.remove(targetUser);
    }

    // 유효성 검사 (생성)
    public boolean isEmailDuplicate(String email) {
        return users.stream()
                    .anyMatch(user -> user.getEmail().equals(email));
    }
}