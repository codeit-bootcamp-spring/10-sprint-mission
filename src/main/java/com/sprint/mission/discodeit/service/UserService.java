package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;
import java.util.List;


public interface UserService {
    UserResponse createUser(UserCreateRequest request); // 계정 생성

    UserResponse getUserById(UUID id); // 단건 조회

    List<UserResponse> getAllUsers(); // 전체 조회

    UserResponse updateUser(UUID id, UserUpdateRequest request); // 계정 정보 수정

    void deleteUser(UUID id); // 계정 삭제
}
