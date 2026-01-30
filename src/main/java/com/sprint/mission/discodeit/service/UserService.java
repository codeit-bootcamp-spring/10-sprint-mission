package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateDTO;
import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import com.sprint.mission.discodeit.dto.user.UserUpdateDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDTO createUser(UserCreateDTO dto);

    List<UserResponseDTO> findAll();

    List<UserResponseDTO> findAllByChannel(UUID channelId);

    UserResponseDTO findByUserId(UUID userId);

    UserResponseDTO updateUser(UserUpdateDTO dto);

    void deleteUser(UUID userId);
}
