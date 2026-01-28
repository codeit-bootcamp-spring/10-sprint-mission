package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateDTO;
import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDTO createUser(UserCreateDTO dto);

    List<User> getUserList();

    List<User> getUsersByChannel(UUID channelId);

    User getUserInfoByUserId(UUID userId);

    User updateUserName(UUID userId, String newName);

    void deleteUser(UUID userId);
}
