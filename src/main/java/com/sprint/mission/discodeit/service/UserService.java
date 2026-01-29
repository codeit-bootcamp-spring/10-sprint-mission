package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import java.util.UUID;
import java.util.List;


public interface UserService {
    UserResponse createAccount(UserCreateRequest request); // 계정 생성

    UserResponse getAccountById(UUID id); // 단건 조회

    List<UserResponse> getAllAccounts(); // 전체 조회

    UserResponse updateAccount(UUID id, UserUpdateRequest request); // 계정 정보 수정

    void deleteAccount(UUID id); // 계정 삭제
}
