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

  // 유저별 참여 채널 목록 조회 시 연관 객체도 함께 가져오기 위한 쿼리
  @Query("SELECT rs FROM ReadStatus rs "
      + "JOIN FETCH rs.channel " // 채널 정보 함께 가져오기
      // 특정 유저의 읽음 상태만 조회
      + "WHERE rs.user.id = :userId")
  List<ReadStatus> findAllByUserIdWithChannel(@Param("userId") UUID userId);

  // ReadStatus와 연관 객체도 함께 가져오기 위한 쿼리 - 단건 조회 (특정 채널 상세/생성 시 사용)
  @Query("SELECT rs FROM ReadStatus rs "
      + "JOIN FETCH rs.user u " // 유저 정보도 함께 가져오기
      + "LEFT JOIN FETCH u.profile " // 해당 유저의 프로필 사진도 함께 가져오기 (없을 수도 있으니 LEFT JOIN)
      // 특정 채널의 읽음 상태만 조회
      + "WHERE rs.channel.id = :channelId")
  List<ReadStatus> findAllByChannelIdWithUser(@Param("channelId") UUID channelId);

  // ReadStatus와 연관 객체도 함께 가져오기 위한 쿼리 - 일괄 조회 (목록 조회 성능 최적화용)
  @Query("SELECT rs FROM ReadStatus rs "
      + "JOIN FETCH rs.channel c " // 채널 정보도 함께 가져오기
      + "JOIN FETCH rs.user u "
      + "LEFT JOIN FETCH u.profile "
      // 리스트에 포함된 채널 ID를 한 번에 조회
      + "WHERE c.id IN :channelIds")
  List<ReadStatus> findAllByChannelIdsWithUser(@Param("channelIds") List<UUID> channelIds);
}
