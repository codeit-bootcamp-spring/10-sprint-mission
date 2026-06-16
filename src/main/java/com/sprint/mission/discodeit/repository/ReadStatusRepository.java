package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {


    @EntityGraph(attributePaths = {"user", "user.status", "user.profile"})
    List<ReadStatus> findAllByChannelId(UUID channelId);

    List<ReadStatus> findAllByUserId(UUID userId);

    @Query("""
    SELECT rs.channel.id
    FROM ReadStatus rs
    WHERE rs.user.id = :userId
""")
    List<UUID> findChannelIdsByUserId(@Param("userId") UUID userId);

    @Query("""
    select rs
    from ReadStatus rs
    join fetch rs.user u
    join fetch u.status
    left join fetch u.profile
    join fetch rs.channel
    where rs.channel.id in :channelIds
""")
    List<ReadStatus> findAllByChannelIdsWithUser(@Param("channelIds") List<UUID> channelIds);

//    위 JPQL 쿼리는 아래의 엔티티 그래프로 대체 가능함(Inner Join이 Left Join이 되는데 현재 로직 상 문제 없음)
//    @EntityGraph(attributePaths = {"user", "user.status", "user.profile", "channel"})
//    List<ReadStatus> findAllByChannelIdIn(UUID channelId);

    @Query("SELECT rs.user.id FROM ReadStatus rs WHERE rs.channel.id = :channelId")
    List<UUID> findParticipantIdsByChannelId(@Param("channelId") UUID channelId);

    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    void deleteByChannelId(UUID channelId);

    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    @EntityGraph(attributePaths = {"user"})
    List<ReadStatus> findAllByChannelIdAndNotificationEnabled(UUID id, boolean b);
}
