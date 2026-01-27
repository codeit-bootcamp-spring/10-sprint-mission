package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface UserRepository {
    User createUser(User user);               // 생성 및 수정 저장
    User findUser(UUID userId);         // 단건 조회
    List<User> findAllUser();           // 전체 조회
    User updateUser(UUID userId, String userName, String userEmail);
    User deleteUser(UUID userId);       // 삭제
}