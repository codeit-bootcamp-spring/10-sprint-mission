package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User registerUser(String name, String email, java.time.LocalDate birthDate, String phoneNumber, String password);
    User findUserById(UUID userId);
    List<User> findAllUser();
    void deleteUser(UUID userId);
    int userCount();


}
