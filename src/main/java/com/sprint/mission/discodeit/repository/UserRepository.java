package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  // N+1 문제 해결을 위한 쿼리
  @Query("SELECT u FROM User u "
      + "LEFT JOIN FETCH u.profile " // 프로필도 함께 가져오기 (없을 수도 있으니 LEFT JOIN)
      + "JOIN FETCH u.userStatus")
  // 상태 정보도 함께 가져오기
  List<User> findAllWithProfileAndStatus();
}
