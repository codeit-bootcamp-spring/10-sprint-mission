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


    @EntityGraph(attributePaths = {"user", "user.profile"})
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
    left join fetch u.profile
    join fetch rs.channel
    where rs.channel.id in :channelIds
""")
    List<ReadStatus> findAllByChannelIdsWithUser(@Param("channelIds") List<UUID> channelIds);

    @Query("SELECT rs.user.id FROM ReadStatus rs WHERE rs.channel.id = :channelId")
    List<UUID> findParticipantIdsByChannelId(@Param("channelId") UUID channelId);

    /**
     * 특정 사용자가 포함된 2인 이하의 비공개 채널과, 그 채널의 '상대방' ID를 한 번에 조회합니다.
     * 결과는 [채널ID, 상대방ID] 형태의 배열 리스트로 반환됩니다.
     */
    @Query("""
        SELECT rs.channel.id, rs2.user.id
        FROM ReadStatus rs
        JOIN ReadStatus rs2 ON rs.channel.id = rs2.channel.id
        WHERE rs.user.id = :userId
        AND rs2.user.id != :userId
        AND rs.channel.type = 'PRIVATE'
        AND (SELECT COUNT(innerRs) FROM ReadStatus innerRs WHERE innerRs.channel.id = rs.channel.id) <= 2
    """)
    List<Object[]> findAffectedPrivateChannelInfo(@Param("userId") UUID userId);

    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    void deleteByChannelId(UUID channelId);

    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    @EntityGraph(attributePaths = {"user"})
    List<ReadStatus> findAllByChannelIdAndNotificationEnabled(UUID id, boolean b);
}
