package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.BinaryContentRecord;
import com.sprint.mission.discodeit.DTO.UserRegitrationRecord;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(UserRegitrationRecord req);
    User find(UUID userId);
    List<User> findAll();
    User update(UUID userId, String newUsername, String newEmail, String newPassword);
    void delete(UUID userId);
}
