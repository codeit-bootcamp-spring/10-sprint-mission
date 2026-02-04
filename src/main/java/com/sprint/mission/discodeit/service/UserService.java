package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

import java.util.UUID;
import java.util.List;


public interface UserService {
    UserResponse create(UserCreateRequest request);

    UserResponse findById(UUID id);

    List<UserResponse> findAll();

    UserResponse update(UUID id, UserUpdateRequest request);

    void deleteById(UUID id);
}
