package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatusType;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 사용자 생성
    public User createUser(String email, String password, String nickname, UserStatusType userStatus);

    // 사용자 단건 조회
    public User searchUser(UUID userId);

    // 사용자 전체 조회
    public List<User> searchUserAll();

    // 사용자 수정
    public User updateUser(UUID userId, String newPassword, String newNickname, UserStatusType newUserStatus);

    // 사용자 삭제
    public void deleteUser(UUID userId);
}
