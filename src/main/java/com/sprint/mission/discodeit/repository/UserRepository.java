package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  @Query("SELECT u FROM User u "
      + "LEFT JOIN FETCH u.profile "
      + "JOIN FETCH u.status")
  List<User> findAllWithProfileAndStatus();

    //권한 존재 확인
    boolean existsByRole(Role role);
    /**
    로그인할때 DiscodeitUserDetailsService.loadUserByUsername에서 findByUsername를 하면
    User의 profile은 fetch = FetchType.LAZY로 설정돼있어서 실제 profile객체가 아닌 proxy 객체가 담긴다.
    profile을 조회하는 동작이 일어나야 실제 profile(BinaryContent)객체가 담긴다.
    profile이 proxy객체인 상태로 userMapper를 통해 profile을 BinaryContentDto로 바꾸려다보니 error가 생겼다.
    ERROR: profile이 proxy여서 DB조회를 통해 실제 BinaryContent를 가져오려했는데 JPA 영속성 context가 만료돼서 못가져옴.

    그래서 fetch join으로 User 조회할때 실제 객체가 담기게끔 해서 해결하려한다.
    **/
  @Query("""
    select u
    from User u
    left join fetch u.profile
    left join fetch u.status
    where u.username = :username
    """)
  Optional<User> findByUsernameWithProfileAndStatus(String username);
}
