package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserService {
    public User createUser(String name, String email, String userId); // 생성
    public User readUser(String userId);
    public List<User> readUsersbyChannel(UUID channelID);
    public User updateUser(String userId, String name, String email);
    public void deleteUser(String userID);
    public List<User> readAllUsers();
    public void joinChannel(String userID, UUID channelID);
    public void exitChannel(String userID, UUID channelID);
    public void save(User user);
    public void setChannelService(ChannelService channelService);
}
