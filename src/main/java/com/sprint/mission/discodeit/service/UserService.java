package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 사용자 관련 기능 정의
    User create(String name, String email, String profileImageUrl);

    void delete(UUID userId);

    User findById(UUID id);

    List<User> findAll();

    User update(UUID id, String name, String email, String profileImageUrl);
}
