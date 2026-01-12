package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 생성
    void createUser(User user);

    // 읽기
    User findById(UUID id);

    // 모두 읽기
    List<User> findAll();

    // 수정
    void updateById(UUID id, String newUserName);

    // 삭제
    void deleteById(UUID id);

    void printAllUsers();
}
