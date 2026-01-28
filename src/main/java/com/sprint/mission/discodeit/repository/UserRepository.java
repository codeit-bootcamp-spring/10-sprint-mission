package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository { // 데이터 관련 로직(저장, 조회, 삭제 등등) 담당
    void save(User user);

    Optional<User> findById(UUID userId);
    Optional<User> findByEmailAndPassword(String email, String password);

    List<User> findAll();

    void delete(UUID userId);

    boolean existByEmail(String newEmail);
    boolean isEmailUsedByOther(UUID userId, String newEmail);
}
