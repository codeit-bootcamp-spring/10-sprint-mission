package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserInfoDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    // CRUD(생성, 읽기, 모두 읽기, 수정, 삭제 기능)
    // C. 생성: userId와 기타 등등 출력
    UserInfoDto createUser(UserCreateDto userCreateDto);

    // R. 읽기
    UserInfoDto findUserById(UUID userId);
    Optional<User> findUserByEmailAndPassword(String email, String password);

    // R. 모두 읽기
    // 모든 사용자
    List<UserInfoDto> findAllUsers();

    // U. 수정
    UserInfoDto updateUserInfo(UserUpdateDto userUpdateDto);

    // D. 삭제
    void deleteUser(UUID userId);
}
