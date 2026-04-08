package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.dto.user.CreateUserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UpdateUserStatusRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequestDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto createUser(CreateUserRequestDTO dto, CreateBinaryContentPayloadDTO profileImage);

    List<UserDto> findAll();

    UserDto findByUserId(UUID userId);

    UserDto updateUserInfo(UUID userId, UpdateUserRequestDTO dto, CreateBinaryContentPayloadDTO profileImage);

    UserDto updateUserStatus(UUID userId, UpdateUserStatusRequestDTO dto);

    void deleteUser(UUID userId);
}
