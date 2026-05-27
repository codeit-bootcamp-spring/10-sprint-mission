package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    @Query(value = "SELECT r FROM ReadStatus AS r " +
            "LEFT JOIN FETCH r.channel " +
            "LEFT JOIN FETCH r.user " +
            "WHERE r.id = :readStatusId")
    Optional<ReadStatus> findByIdWithUserAndChannel(@Param("readStatusId") UUID readStatusId);

    @Query(value = "SELECT r FROM ReadStatus AS r " +
            "LEFT JOIN FETCH r.channel " +
            "LEFT JOIN FETCH r.user " +
            "Where r.user.id = :userId")
    List<ReadStatus> findAllByUserIdWithUserAndChannel(@Param("userId") UUID userId);

    @Query(value = "SELECT r FROM ReadStatus AS r " +
            "LEFT JOIN FETCH r.channel " +
            "LEFT JOIN FETCH r.user AS u " +
            "LEFT JOIN FETCH u.profile " +
            "Where r.channel.id = :channelId")
    List<ReadStatus> findAllByChannelIdWithUserAndChannel(@Param("channelId") UUID channelId);

    @Query(value = "SELECT r FROM ReadStatus AS r " +
            "LEFT JOIN FETCH r.channel " +
            "LEFT JOIN FETCH r.user AS u " +
            "LEFT JOIN FETCH u.profile " +
            "Where r.channel.id IN :channelIds")
    List<ReadStatus> findAllByChannelIdsWithUserAndChannel(@Param("channelIds") List<UUID> channelIds);

    void deleteByChannelId(UUID channelId);

    boolean existsReadStatusByUserIdAndChannelId(UUID userId, UUID channelId);
}
