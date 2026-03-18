package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    Optional<Channel> findByName(String name);

    @Query("SELECT new com.sprint.mission.discodeit.dto.ChannelDto$RemoveParticipants" +
            "(c.id, c.type, c.name, c.description, MAX(m.createdAt)) FROM Channel c " +
            "LEFT JOIN Message m ON c.id = m.channel.id " +
            "LEFT JOIN ReadStatus rs ON c.id = rs.channel.id AND rs.user.id = :userId " +
            "WHERE c.type = 'PUBLIC' OR rs.id IS NOT NULL " +
            "GROUP BY c.id, c.type, c.name, c.description")
    List<ChannelDto.RemoveParticipants> findAllByUserId(UUID userId);
}
