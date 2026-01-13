package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String nickName, String userName, String email, String phoneNumber);

    User findById(UUID id);

    User findByUserName(String userName); // 이름으로 조회(단건)

    List<User> findAllUser(); // 전체 조회(다건)

    User updateUser(UUID userId, User user); // 수정하고 싶은 필드에 null 이외의 값을 넣는다.

    void delete(UUID userId);
}
