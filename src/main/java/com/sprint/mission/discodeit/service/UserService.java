package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(String accountId, String password, String name, String mail);
    User getUser(UUID uuid);
    Optional<User> findUserByAccountId(String accountId);
    Optional<User> findUserByMail(String mail);
    List<User> findAllUsers();
    User updateUser(UUID uuid, String accountId, String password, String name, String mail);
    void deleteUser(UUID uuid);
    void deleteUserByAccountId(String accountId);
    void deleteUserByMail(String mail);
}
