package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface UserService {

    UserDto createUser(
            UserCreateRequest createUserRequest,
            BinaryContentRequest profileImage
    );

    UserDto findUserByUserID(UUID userId);

    List<UserDto> findAllUsers();

    UserDto updateUser(
            UUID requestId,
            UserUpdateRequest updateUserRequest,
            BinaryContentRequest profileImage
    );

    void deleteUser(UUID requestId);
}
