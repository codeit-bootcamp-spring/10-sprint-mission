package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {
    List<ReadStatus> findAllByUserId(UUID userId);

    @EntityGraph(attributePaths = {"user", "user.profile", "user.status"})
    List<ReadStatus> findAllByChannelId(UUID channelId);

    @EntityGraph(attributePaths = {"user", "user.profile", "user.status"})
    List<ReadStatus> findAllByChannelIdIn(List<UUID> channelIds);

    void deleteAllByChannelId(UUID channelId);
}