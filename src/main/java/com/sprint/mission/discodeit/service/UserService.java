package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    //User create(String username, String email, String password);
    UserResponseDto create(UserCreateDto userCreateDto);
    UserResponseDto find(UUID userId);
    List<UserResponseDto> findAll();
    //User update(UUID userId, String newUsername, String newEmail, String newPassword);
    UserResponseDto update(UUID userId, UserUpdateDto userUpdateDto);
    void delete(UUID userId);
}
