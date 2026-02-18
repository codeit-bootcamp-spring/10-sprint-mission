package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateUserRequestDto;
import com.sprint.mission.discodeit.dto.UpdateUserRequestDto;
import com.sprint.mission.discodeit.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserDto create(CreateUserRequestDto request);
    Optional<UserDto> findById(UUID userId);
    UserDto find(UUID userId);
    List<UserDto> findAll();
    UserDto update(UUID userId, UpdateUserRequestDto request);
    void delete(UUID userId);
}
