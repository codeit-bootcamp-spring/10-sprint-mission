package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatusType;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 사용자 생성
    User createUser(String email, String password, String nickname, UserStatusType userStatus);

    // 사용자 단건 조회
    User searchUser(UUID userId);

    // 사용자 전체 조회
    List<User> searchUserAll();

    // 채널 내 멤버 목록 조회
    List<User> searchMembersByChannelId(UUID channelId);

    // 사용자 수정
    User updateUser(UUID userId, String newPassword, String newNickname, UserStatusType newUserStatus);

    // 사용자 저장
    void updateUser(UUID userId, User user);

    // 사용자 삭제
    void deleteUser(UUID userId);
}
