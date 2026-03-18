package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  List<ReadStatus> findAllByUserId(UUID userId);

  List<ReadStatus> findAllByChannelId(UUID channelId);

  void deleteAllByChannelId(UUID channelId);

  boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

  @Query("""
      SELECT rs
      FROM ReadStatus rs
      JOIN FETCH rs.channel c
      JOIN FETCH rs.user u
      LEFT JOIN FETCH u.profile
      LEFT JOIN FETCH u.userStatus
      WHERE c.id IN :channelIds
      """)
  List<ReadStatus> findAllByChannelIdInWithUser(@Param("channelIds") List<UUID> channelIds);
}