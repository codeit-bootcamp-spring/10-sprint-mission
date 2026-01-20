package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatusType;

import java.util.List;
import java.util.UUID;

public interface UserService {
    public User createUser(String email, String password, String nickname, UserStatusType userStatus);

    public User searchUser(UUID userId);

    public List<User> searchUserAll();

    public User updateUser(UUID userId, String newPassword, String newNickname, UserStatusType newUserStatus);

    public void deleteUser(UUID userId);
}
