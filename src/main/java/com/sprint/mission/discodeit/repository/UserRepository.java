package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u\n" +
            "    FROM ReadStatus rs\n" +
            "    JOIN rs.user u\n" +
            "    LEFT JOIN FETCH u.profile\n" +
            "    WHERE rs.channel.id = :channelId")
    List<User> findAllByChannelId(@Param("channelId") UUID channelId);

    @Query("SELECT rs\n" +
            "    FROM ReadStatus rs\n" +
            "    JOIN FETCH rs.user u\n" +
            "    LEFT JOIN FETCH u.profile\n" +
            "    JOIN FETCH rs.channel\n" +
            "    WHERE rs.channel.id IN :channelIds")
    List<ReadStatus> findAllByChannelIdIn(@Param("channelIds") List<UUID> channelIds);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile")
    List<User> findAllWithProfileAndStatus();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.username = :username")
    Optional<User> findByUsernameWithProfile(@Param("username") String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.id = :id")
    Optional<User> findByIdWithProfile(@Param("id") UUID id);


    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByRole(Role role);
}
