package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserService {

    User CreateUser(String userName, String email);

    User findId(UUID id);
    User findEmail(String email);
    List<User> findAll();

    User updateName(User user, String userName);
    User updateEmail(User user, String email);

    void delete(User id);

}
