package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(String accountId, String password, String name, String mail);
    // get: uuid로 검색하는건 확실하게 User반환 or Throw
    User getUser(UUID uuid);
    // find: 그 외의 필드로 검색하는건 Optional<User>로 호출한 쪽에서 분기처리
    Optional<User> findUserByAccountId(String accountId);
    Optional<User> findUserByMail(String mail);
    List<User> findAllUsers();
    User updateUser(UUID uuid, String accountId, String password, String name, String mail);
    User updateUser(User newUser);     // File쪽 특화용(다른 JCF관련 필드)
    void deleteUser(UUID uuid);
    void deleteUserByAccountId(String accountId);
    void deleteUserByMail(String mail);
}
