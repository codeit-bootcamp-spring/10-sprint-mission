package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.UUID;

public interface UserService {
    public User createUser(String email, String password, String nickname);
    public User searchUser(UUID userId);
    public ArrayList<User> searchUserAll();
    public void updateUser(UUID userId, String newPassword, String newNickname);
    public void deleteUser(UUID userId);
}
