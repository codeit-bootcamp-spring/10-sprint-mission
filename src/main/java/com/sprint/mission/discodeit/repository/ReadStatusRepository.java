package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    List<ReadStatus> findAllByUser_Id(UUID userId);

    List<ReadStatus> findAllByChannel_Id(UUID channelId);

    Optional<ReadStatus> findByUser_IdAndChannel_Id(UUID userId, UUID channelId);

    boolean existsByUser_IdAndChannel_Id(UUID userId, UUID channelId);

    UUID user(User user);
}
