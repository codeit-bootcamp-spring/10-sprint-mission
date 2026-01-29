package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    // CRUD(생성, 읽기, 모두 읽기, 수정, 삭제 기능)
    // C. 생성: userId와 기타 등등 출력
    UserResponse createUser(UserCreateRequest userCreateRequest);

    // R. 읽기
    UserResponse findUserById(UUID userId);
    Optional<User> findUserByUserNameAndPassword(String userName, String password);

    // R. 모두 읽기
    // 모든 사용자
    List<UserResponse> findAllUsers();

    // U. 수정
    UserResponse updateUserInfo(UserUpdateRequest userUpdateRequest);

    // D. 삭제
    void deleteUser(UUID userId);
}
