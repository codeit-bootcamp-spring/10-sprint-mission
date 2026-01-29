package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UUID createUser(CreateUserRequest createUserRequest);

    UserResponse findUserByUserID(UUID userId);

    List<UserResponse> findAllUsers();

    UserResponse updateUser(UUID requestId, UpdateUserRequest updateUserRequest);

    void deleteUser(UUID requestId);
}
