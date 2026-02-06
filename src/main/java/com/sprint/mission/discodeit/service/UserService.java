package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // Create
    UserResponseDto create(UserCreateDto request);

    // Read
    UserResponseDto findById(UUID id);

    // ReadAll
    List<UserResponseDto> findAll();

//    List<UserInfoDto> findAllByChannelId(UUID channelId);

    // Update
    UserResponseDto update(UUID id, UserUpdateDto request);

    // Delete
    void delete(UUID id);

    void updateLastActiveTime(UUID id);
}
