package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateField;

public class JCFUserService implements UserService {
    public static final ArrayList<User> users = new ArrayList<>();         // 전체 사용자 리스트

    // 사용자 생성
    @Override
    public User createUser(String email, String password, String nickname) {
        if (isEmailDuplicate(email)){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User newUser = new User(email, password, nickname);
        users.add(newUser);
        return newUser;
    }

    // 사용자 단건 조회
    @Override
    public User searchUser(UUID targetUserId) {
        for (User user : users) {
            if (user.getId().equals(targetUserId)) {
                return user;
            }
        }
        throw new IllegalArgumentException("해당 사용자가 존재하지 않습니다.");
    }

    // 사용자 전체 조회
    @Override
    public ArrayList<User> searchUserAll() {        return users;       }

    // 사용자 정보 수정 (비밀번호, 닉네임)
    @Override
    public void updateUser(UUID targetUserId, String newPassword, String newNickname) {
        User targetUser = searchUser(targetUserId);

        validateField(newPassword, "[비밀 번호 변경 실패] 올바른 비밀 번호가 아닙니다.");
        validateField(newNickname, "[닉네임 변경 실패] 올바른 닉네임이 아닙니다.");

        targetUser.updatePassword(newPassword);
        targetUser.updateNickname(newNickname);
    }

    // 사용자 삭제
    @Override
    public void deleteUser(UUID targetUserId) {
        User targetUser = searchUser(targetUserId);

        users.remove(targetUser);
    }

    // 유효성 검사 (생성)
    public boolean isEmailDuplicate(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }
}