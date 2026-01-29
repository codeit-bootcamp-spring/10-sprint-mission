package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(UserCreateRequestDto userCreateRequestDto);
    UserResponseDto find(UUID userId);
    List<UserResponseDto> findAll();
    User update(UserUpdateRequestDto userUpdateRequestDto);
    void delete(UUID userId);
}
