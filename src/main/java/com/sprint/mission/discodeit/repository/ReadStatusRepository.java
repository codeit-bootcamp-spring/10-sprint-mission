package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {
    List<ReadStatus> findAllByUser_Id(UUID userId);

    boolean existsByUser_IdAndChannel_Id(UUID userId, UUID channelId);

    void deleteAllByChannelId(UUID channelId);
}
