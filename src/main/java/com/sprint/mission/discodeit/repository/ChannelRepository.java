package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    @Query(value = "SELECT DISTINCT c FROM Channel AS c " +
            "LEFT JOIN ReadStatus AS r ON r.channel = c " +
            "WHERE c.type = :type OR r.user.id = :userId")
    List<Channel> findChannelByUserId(
            @Param("type") ChannelType type,
            @Param("userId") UUID userId);
}
