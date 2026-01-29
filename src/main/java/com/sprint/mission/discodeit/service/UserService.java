package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponseDto create(UserCreateDto dto);
    UserResponseDto find(UUID userId);
    List<UserResponseDto> findAll();
    UserResponseDto update(UUID userId, UserUpdateDto dto);
    void delete(UUID userId);
}
