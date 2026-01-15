package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

// 추후 DB 사용을 염두해 interface 사용
public interface UserService {
    // Create
    /**
     * 사용자 등록 / 유저를 생성 / 유저가 회원가입을 하여 계정(User)을 생성한다.
     * @param username 사용자 이름(String)
     * @return 생성된 User 객체
     * @throws IllegalArgumentException 이미 존재하는 이름일 경우 발생
     */
    User createUser(String username);

    // Read
    /**
     * 사용자 단건 조회 / 유저가 특정 유저를 조회한다. (친구 추가 등을 위해)
     * @param id 조회할 사용자의 id(UUID)
     * @return 조회된 User 객체
     * @throws IllegalArgumentException 해당 ID의 사용자가 없을 경우 발생
     */
    User findUserByUserId(UUID id); // 메서드 네이밍 변경

    /**
     * 사용자 전체 조회 (시스템관리자용)
     * param 없음
     * @return 전체 사용자 목록
     * throws 구현 필요
     */
    List<User> findAllUsers();

    // Update
    /**
     * 사용자 정보 수정 / 유저 이름 변경 / 유저가 이름을 변경한다.
     * @param id 대상 사용자 ID(UUID)
     * @param newUsername 변경할 새로운 이름(String)
     * @return 수정된 사용자 객체(User)
     * throws 구현 필요
     */
    User updateUser(UUID id, String newUsername);

    // Delete
    /**
     * 사용자 삭제 / 유저 탈퇴 / 유저가 탈퇴한다. / 시스템 관리자가 해당 유저를 삭제시킨다.
     * @param id 삭제할 사용자 ID
     * return 없음
     * throws IllegalArgumentException findUserByIdOrThrow에서 해당 id의 사용자가 존재하지 않을 경우 예외 발생 (없는 유저 삭제 시도)
     */
    void deleteUser(UUID id);
}