package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    // 참조하는 user는 결국 한 명이라 n개가 생기지 않음 -> 그래서 channel만 attributePaths로 추가한 것
    @EntityGraph(attributePaths = "channel")
    List<ReadStatus> findAllByUser_Id(UUID userId);

    List<ReadStatus> findAllByChannel_Id(UUID channelId);

    @EntityGraph(attributePaths = {"user", "channel"})      // n+1 문제 해결
    List<ReadStatus> findAllByChannel_IdIn(List<UUID> channelIds);

    Optional<ReadStatus> findByUser_IdAndChannel_Id(UUID userId, UUID channelId);

    boolean existsByUser_IdAndChannel_Id(UUID userId, UUID channelId);
}
