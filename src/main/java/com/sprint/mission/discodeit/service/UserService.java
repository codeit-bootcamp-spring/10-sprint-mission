package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User createUser(UserCreateRequest createUserRequest, BinaryContentRequest profileImage);

    User findUserByUserID(UUID userId);

    List<UserDto> findAllUsers();

    User updateUser(UUID requestId, UserUpdateRequest updateUserRequest, BinaryContentRequest profileImage);

    void deleteUser(UUID requestId);
}
