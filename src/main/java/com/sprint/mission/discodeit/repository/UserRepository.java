package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID userId);
    List<User> findAll();
    void delete(UUID userId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByEmailExceptUserId(String email, UUID exceptUserId);
    boolean existsByNicknameExceptUserId(String nickname, UUID exceptUserId);
    Optional<User> findByEmailAndPassword(String email, String password);
}
