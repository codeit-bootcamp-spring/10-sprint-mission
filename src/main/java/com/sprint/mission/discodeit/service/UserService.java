package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(String userName, String userEmail, String userPassword);
    User readUser(UUID id);
    List<User> readAllUser();
    User updateUser(UUID id,String userName, String userEmail, String userPassword);

}
