package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse createUser(UserCreateRequest request);
    UserResponse getUser(UUID id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(UserUpdateRequest request);
    void deleteUser(UUID id);
}