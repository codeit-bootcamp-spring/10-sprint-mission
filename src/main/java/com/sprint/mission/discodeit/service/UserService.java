package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserService {

    User CreateUser(String userName, String email);

    User findId(UUID id);
    User findEmail(String email);
    List<User> findAll();

    User updateName(UUID user, String userName);
    User updateEmail(UUID user, String email);

    void delete(UUID id);

}
