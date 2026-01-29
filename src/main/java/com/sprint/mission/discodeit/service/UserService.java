package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 사용자 관련 기능 정의
    UserDto.UserResponse create(UserDto.UserCreateRequest request);
    UserDto.UserResponse findById(UUID id);
    List<UserDto.UserResponse> findAll();
    UserDto.UserResponse update(UUID userId, UserDto.UserUpdateRequest request);
    void delete(UUID userId);
}
