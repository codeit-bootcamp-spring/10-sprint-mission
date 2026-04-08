package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.ChannelDto.ChannelSummary;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  boolean existsByName(String name);

  @Query("SELECT new com.sprint.mission.discodeit.dto.ChannelDto$ChannelSummary" +
      "(c.id, c.type, c.name, c.description, MAX(m.createdAt)) FROM Channel c " +
      "LEFT JOIN Message m ON c.id = m.channel.id " +
      "LEFT JOIN ReadStatus rs ON c.id = rs.channel.id AND rs.user.id = :userId " +
      "WHERE c.type = 'PUBLIC' OR rs.id IS NOT NULL " +
      "GROUP BY c.id, c.type, c.name, c.description")
  List<ChannelSummary> findAllByUserId(UUID userId);
}
