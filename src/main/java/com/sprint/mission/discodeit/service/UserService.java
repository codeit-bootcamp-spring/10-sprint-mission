package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import java.util.UUID;
import java.util.List;


public interface UserService {
    UserResponse signUp(UserCreateRequest request); // 회원가입

    UserResponse getUserById(UUID id); // 단건 조회

    List<UserResponse> getAllUsers(); // 전체 조회

    UserResponse updateProfile(UUID id, UserUpdateRequest request); // 프로필 수정

    void deleteUser(UUID id); // 회원 탈퇴

    UserResponse login(LoginRequest request); // 로그인
}
