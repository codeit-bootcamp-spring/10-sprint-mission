package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserResponseDTO;
import com.sprint.mission.discodeit.dto.userdto.UserUpdateDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserResponseDTO create(UserCreateRequestDTO req, BinaryContentDTO profileDto);

    UserResponseDTO find(UUID userId);

    List<UserResponseDTO> findAll();

    UserResponseDTO update(UUID userId, UserUpdateDTO userUpdateDTO, BinaryContentDTO profileDto);

    void delete(UUID userId);
}
