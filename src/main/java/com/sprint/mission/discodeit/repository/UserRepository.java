package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// 데이터 관련 로직(저장, 조회, 삭제 등등) 담당
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query(value = "SELECT u FROM User AS u " +
            "LEFT JOIN FETCH u.profile " +
            "WHERE u.id = :userId")
    Optional<User> findByIdWithProfile(@Param("userId") UUID userId);

    @Query(value = "SELECT u FROM User AS u " +
            "LEFT JOIN FETCH u.profile " +
            "WHERE u.username = :username")
    Optional<User> findByUsernameWithProfile(@Param("username") String username);

    @Query(value = "SELECT u FROM User AS u " +
            "LEFT JOIN FETCH u.profile ")
    List<User> findAllWithProfile();

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);


    @Query(value = "SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM User AS u " +
            "WHERE u.email = :email " +
            "  AND u.id != :userId ")
    boolean isEmailUsedByOther(@Param("userId") UUID userId, @Param("email") String newEmail);

    @Query(value = "SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM User AS u " +
            "WHERE u.username = :username " +
            "  AND u.id != :userId ")
    boolean isUsernameUsedByOther(@Param("userId") UUID userId, @Param("username") String newUsername);

    boolean existsByRole(Role role);
}
