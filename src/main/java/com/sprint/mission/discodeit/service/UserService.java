package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto create(UserCreateDto userCreateDto);
    UserDto find(UUID userId);
    List<UserDto> findAll();
    UserDto update(UUID userId, UserUpdateDto userUpdateDto);
    void delete(UUID userId);
}
