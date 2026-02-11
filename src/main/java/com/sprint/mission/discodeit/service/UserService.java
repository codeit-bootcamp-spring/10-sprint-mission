package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(UserCreateDto userCreateDto);
    UserResponseDto find(UUID userId);
    List<UserResponseDto> findAll();
    UserResponseDto update(UUID userId, UserUpdateDto userUpdateDto);
    void delete(UUID userId);
}
