package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserRequestCreateDto;
import com.sprint.mission.discodeit.dto.UserRequestUpdateDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(UserRequestCreateDto request);

    UserResponseDto find(UUID id);

    List<UserResponseDto> findAll();

    UserResponseDto update(UserRequestUpdateDto request);

    void deleteUser(UUID id);

    List<User> readUsersByChannel(UUID channelId);



}
