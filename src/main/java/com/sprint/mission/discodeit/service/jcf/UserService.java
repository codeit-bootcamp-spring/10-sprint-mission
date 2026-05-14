package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserService {

  User createUser(String name, String email, String userId); // 생성

  User readUser(String userId);

  List<User> readUsersbyChannel(UUID channelID);

  User updateUser(String userId, String name, String email);

  void deleteUser(String userID);

  List<User> readAllUsers();

  void joinChannel(String userID, UUID channelID);

  void exitChannel(String userID, UUID channelID);

  void save(User user);

  void setChannelService(ChannelService channelService);
}
