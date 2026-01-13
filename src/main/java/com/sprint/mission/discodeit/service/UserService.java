package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserService {
    //  CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능

    void CreateUser(User user);

    User findId(UUID id);

    List<User> findAll();

    void updateName(User user, String userName);
    void updateEmail(User user, String email);

    void changeEmail(User user, String email);

    void delete(UUID id);

}
