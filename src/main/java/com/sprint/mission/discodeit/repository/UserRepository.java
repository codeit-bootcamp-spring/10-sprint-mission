package com.sprint.mission.discodeit.repository;


import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserRepository {
    User save(User user);                  // 생성 / 수정 통합
    Optional<User> findById(UUID id);      // 단건 조회
    List<User> findAll();                  // 전체 조회
    void delete(UUID id);                  // 삭제
    boolean existsByName(String name);     // 이름 중복
    boolean existsByEmail(String email);   // 이메일 중복

}