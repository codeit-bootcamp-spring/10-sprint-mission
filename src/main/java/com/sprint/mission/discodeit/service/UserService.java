package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public interface UserService {
    public User createUser(String name, String email, String userId); // 생성
    public User readUser(String userId); // 조회
    public User readUser(UUID id);
    public boolean updateUser(String userId, String name, String email);
    public boolean deleteUser(UUID uuId);
    public boolean deleteUser(String userName);
    public ArrayList<User> readAllUsers();
}
