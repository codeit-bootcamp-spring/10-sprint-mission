package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateDTO;
import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import com.sprint.mission.discodeit.dto.user.UserUpdateDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDTO createUser(UserCreateDTO dto);

    List<UserResponseDTO> getUserList();

    List<UserResponseDTO> getUsersByChannel(UUID channelId);

    UserResponseDTO getUserInfoByUserId(UUID userId);

    UserResponseDTO updateUserName(UserUpdateDTO dto);

    UserResponseDTO updateUserStatus(UserUpdateDTO dto);

    void deleteUser(UUID userId);
}
