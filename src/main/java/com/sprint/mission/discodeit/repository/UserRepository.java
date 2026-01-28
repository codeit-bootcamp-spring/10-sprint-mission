package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID userId);
    List<User> findAll();
    void save(UUID userId , User user);
    void delete(UUID userId);
}
