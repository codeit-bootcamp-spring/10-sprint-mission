package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {

  UserResponse create(UserCreateRequest request);

  UserResponse find(UUID userId);

  List<UserResponse> findAll();

  UserResponse update(UserUpdateRequest request);

  UserResponse updateRole(UserRoleUpdateRequest request);

  void delete(UUID userId);

  List<UserDto> findAllDto();

  User findEntity(UUID userId);
}
