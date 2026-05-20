package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  // 유저 목록 조회 시 연관 객체도 함께 가져오기 위한 쿼리
  @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile")
  // 프로필도 함께 가져오기 (없을 수도 있으니 LEFT JOIN)
  List<User> findAllWithProfile();

  // 로그인 시 유저를 찾을 때 연관 객체도 함께 가져오기 위한 쿼리
  @Query("SELECT u FROM User u "
      + "LEFT JOIN FETCH u.profile " // 프로필도 함께 가져오기 (없을 수도 있으니 LEFT JOIN)
      + "WHERE u.username = :username")
  Optional<User> findByUsernameWithProfile(@Param("username") String username);

  // ID로 조회 시 연관 객체도 함께 가져오기 위한 쿼리
  @Query("SELECT u FROM User u "
      + "LEFT JOIN FETCH u.profile "
      + "WHERE u.id = :id")
  Optional<User> findByIdWithProfile(@Param("id") UUID id);

  // 특정 권한을 가진 유저의 존재 여부 확인
  boolean existsByRole(Role role);
}
