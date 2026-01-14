package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public interface UserService {
    public User createUser(String name, String email, String userId); // 생성
    public User readUser(String userId);

    public User updateUser(String userId, String name, String email);
    public void deleteUser(String userID);
    public ArrayList<User> readAllUsers();
    public void joinChannel(String userID, UUID channelID);
    public void exitChannel(String userID, UUID channelID);
}
