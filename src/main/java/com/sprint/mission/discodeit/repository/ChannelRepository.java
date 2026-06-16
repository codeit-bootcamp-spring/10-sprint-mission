package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    @Modifying(clearAutomatically = true)
    @Query("""
    DELETE FROM Channel c
    WHERE c.id IN :channelIds
    AND c.type = 'PRIVATE'
    AND (SELECT COUNT(rs) FROM ReadStatus rs WHERE rs.channel = c) <= 1
    """)
    void deleteEmptyOrLonelyChannels(@Param("channelIds") List<UUID> channelIds);

    @Query("""
    SELECT DISTINCT c FROM Channel c
    LEFT JOIN ReadStatus rs ON rs.channel = c
    WHERE c.type = 'PUBLIC' OR rs.user.id = :userId
    """)
    List<Channel> findAllAccessibleByUserId(@Param("userId") UUID userId);

    @Query("""
    SELECT c FROM Channel c
    JOIN ReadStatus rs ON rs.channel = c
    WHERE c.type = 'PRIVATE'
    AND rs.user.id IN :participantIds
    GROUP BY c.id
    HAVING COUNT(rs.user.id) = :participantCount
    AND COUNT(rs.user.id) = (SELECT COUNT(rs2) FROM ReadStatus rs2 WHERE rs2.channel = c)
    """)
    List<Channel> findPrivateChannelByParticipants(@Param("participantIds") java.util.Collection<UUID> participantIds, @Param("participantCount") long participantCount);
}
