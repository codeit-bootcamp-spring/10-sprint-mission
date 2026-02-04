package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserInfoDto;
import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // Create
    UserInfoDto create(UserCreateDto request);

    // Read
    UserInfoDto findById(UUID id);

    // ReadAll
    List<UserInfoDto> findAll();

//    List<UserInfoDto> findAllByChannelId(UUID channelId);

    // Update
    UserInfoDto update(UserUpdateDto request);

    // Delete
    void delete(UUID id);

    void updateLastActiveTime(UUID id);
}
