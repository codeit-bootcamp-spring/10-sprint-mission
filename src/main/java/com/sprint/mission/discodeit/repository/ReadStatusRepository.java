package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  Optional<ReadStatus> findByUserIdAndChannelId(UUID userId,
      UUID channelId);

  List<ReadStatus> findAllByUserId(UUID userId);

  List<ReadStatus> findAllByChannelId(UUID channelId);

  void deleteByUserId(UUID userId);

  void deleteByChannelId(UUID channelId);

  // N+1 문제 해결을 위한 쿼리
  @Query("SELECT rs FROM ReadStatus rs "
      + "JOIN FETCH rs.channel " // 채널 정보 함께 가져오기
      // 특정 유저의 읽음 상태만 조회
      + "WHERE rs.user.id = :userId")
  List<ReadStatus> findAllByUserIdWithChannel(@Param("userId") UUID userId);
}
