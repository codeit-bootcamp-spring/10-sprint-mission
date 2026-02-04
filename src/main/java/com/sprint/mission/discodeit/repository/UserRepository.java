package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    // 추후 JPA 도입을 고려한 메서드 이름 사용
    User save(User user);
    Optional<User> findById(UUID id);
    List<User> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
