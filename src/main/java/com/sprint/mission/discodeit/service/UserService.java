package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // DTO로 파라미터 그룹화 (요구사항)
    UserResponse create(CreateUserRequest request);

    // UserResponse 반환 (패스워드 제외, 온라인 상태 포함)
    UserResponse find(UUID userId);
    List<UserResponse> findAll();

    // DTO로 파라미터 그룹화
    UserResponse update(UUID userId, UpdateUserRequest request);

    void delete(UUID userId);
}
