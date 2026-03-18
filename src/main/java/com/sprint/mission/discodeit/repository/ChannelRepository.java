package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  @Query("""
      SELECT c
      FROM Channel c
      WHERE c.type = com.sprint.mission.discodeit.entity.ChannelType.PUBLIC
         OR EXISTS (
           SELECT 1
           FROM ReadStatus rs
           WHERE rs.channel = c
             AND rs.user.id = :userId
         )
      """)
  List<Channel> findAllVisibleByUserId(@Param("userId") UUID userId);
}