package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 기본 CRUD 메서드 이름은 심플하게
    UserResponse create(UserCreateRequest request);
    UserResponse find(UUID userId);
    List<UserResponse> findAll();
    UserResponse update(UUID userId, UserUpdateRequest request);
    void delete(UUID userId);

    List<UserDto> findAllUsers();
    // 비즈니스 로직이 명확한 경우 구체적인 메서드명 사용
}
