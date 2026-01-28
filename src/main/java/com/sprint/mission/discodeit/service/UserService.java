package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.UserFindingDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserCreateRequestDto userCreateRequestDto);
    UserFindingDto find(UUID userId);
    List<UserFindingDto> findAll();
    User update(UUID userId, String newUsername, String newEmail, String newPassword);
    void delete(UUID userId);
}
