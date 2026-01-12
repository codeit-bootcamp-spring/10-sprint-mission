package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

public class JCFUserService implements UserService {
    // 사용자 리스트
    public final ArrayList<User> users = new ArrayList<>();

    // 사용자 생성
    @Override
    public User createUser(String email, String password, String nickname) {
        // 1. 유효성 검사 필요

        // 2. 사용자 이메일 중복 방지
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                System.out.println("이미 존재하는 사용자입니다.");
                return null;
            }
        }

        // 3. User 생성
        User newUser = new User(email, password, nickname);
        users.add(newUser);
        return newUser;
    }

    // 사용자 단건 조회
    @Override
    public User searchUser(UUID targetUserId) {
        // 1, 유저 탐색
        for (User user : users){
            // 해당 유저가 있으면 반환
            if (user.getId().equals(targetUserId)) {
                return user;
            }
        }
        // 없으면 널 반환
        System.out.println("해당 유저가 존재하지 않습니다.");
        return null;
    }

    // 사용자 전체 조회
    @Override
    public ArrayList<User> searchUserAll() {
        return users;
    }

    // 사용자 정보 수정 (비밀번호, 닉네임)
    @Override
    public void updateUser(UUID targetUserId, String newPassword, String newNickname) {
        // 1. 유저 탐색
        User targetUser = searchUser(targetUserId);

        // 2. 업데이트
        if (targetUser != null) {
            if (newPassword != null || newPassword.isBlank()) {
                targetUser.updatePassword(newPassword);
            }
            else {
                System.out.println("잘못된 비밀번호입니다.");
            }
            if (newNickname != null || newNickname.isBlank()){   // 동시에 둘 다 변경할 수 있으므로 if
                targetUser.updateNickname(newNickname);
            }
            else {
                System.out.println("잘못된 닉네임입니다.");
            }
        }
    }

    // 사용자 삭제
    @Override
    public void deleteUser(UUID targetUserId) {
        // 1. 유저 탐색
        User targetUser = searchUser(targetUserId);

        // 2. 유저 삭제
        if (targetUser != null) {
            users.remove(targetUser);
        }
    }
}