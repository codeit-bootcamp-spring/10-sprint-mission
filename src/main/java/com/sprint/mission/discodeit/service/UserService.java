package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.CreateUserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequestDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDTO createUser(CreateUserRequestDTO dto);

    List<UserResponseDTO> findAll();

    List<UserResponseDTO> findAllByChannel(UUID channelId);

    UserResponseDTO findByUserId(UUID userId);

    UserResponseDTO updateUser(UpdateUserRequestDTO dto);

    void deleteUser(UUID userId);
}
