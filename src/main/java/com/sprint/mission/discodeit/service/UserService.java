package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.*;

// 추후 DB 사용을 염두해 interface 사용
public interface UserService {
    // Create
    /**
     * 사용자 등록
     * @param username 사용자 이름
     * @return 생성된 사용자 객체
     */
    User createUser(String username);

    // Read
    /**
     * 사용자 단건 조회
     * @param id 조회할 사용자의 UUID
     * @return 존재하면 User 객체, 없으면 빈 Optional
     */
    Optional<User> findOne(UUID id);

    /**
     * 사용자 전체 조회
     * @return 전체 사용자 목록
     */
    List<User> findAll();

    // Update
    /**
     * 사용자 정보 수정
     * @param id 대상 사용자 ID
     * @param newUsername 변경할 새로운 이름
     * @return 수정된 사용자 객체
     */
    User updateUser(UUID id, String newUsername);

    // Delete
    /**
     * 사용자 삭제
     * @param id 삭제할 사용자 ID
     */
    void deleteUser(UUID id);
}