package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {

    User createUser(String userName, String userEmail);

    User findUser(UUID userId);

    List<User> findAllUser();

    User updateUser(UUID userId, String userName, String userEmail);

    void deleteUser(UUID userId);
}
