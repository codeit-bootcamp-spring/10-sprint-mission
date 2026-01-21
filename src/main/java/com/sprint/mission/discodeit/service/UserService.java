package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    // CRUD(생성, 읽기, 모두 읽기, 수정, 삭제 기능)
    // C. 생성: userId와 기타 등등 출력
    User createUser(String email, String userName, String nickName, String password, String birthday);

    // R. 읽기
    User findUserById(UUID userId);
    Optional<User> findUserByEmailAndPassword(String email, String password);

    // R. 모두 읽기
    // 모든 사용자
    List<User> findAllUsers();

    // U. 수정
    User updateUserInfo(UUID userId, String email, String password, String userName, String nickName, String birthday);

    // D. 삭제
    void deleteUser(UUID userId);
}
