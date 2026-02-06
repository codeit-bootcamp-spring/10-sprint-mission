package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserWithOnlineResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // CRUD(생성, 읽기, 모두 읽기, 수정, 삭제 기능)
    // C. 생성: userId와 기타 등등 출력
    User createUser(UserCreateRequest userCreateRequest);

    // R. 읽기
    UserWithOnlineResponse findUserById(UUID userId);

    // R. 모두 읽기
    // 모든 사용자
    List<UserWithOnlineResponse> findAllUsers();

    // U. 수정
    User updateUserInfo(UserUpdateRequest userUpdateRequest);

    // D. 삭제
    void deleteUser(UUID userId);
}
