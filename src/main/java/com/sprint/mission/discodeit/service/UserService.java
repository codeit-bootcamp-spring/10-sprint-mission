package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public interface UserService {
    public User createUser(String name, String email, String userId); // 생성
    public User readUser(String userId);
    public User readUser(UUID uuid);
    public User updateUser(UUID uuid,String userId, String name, String email);
    public User deleteUser(UUID uuId);
    public ArrayList<User> readAllUsers();
}
