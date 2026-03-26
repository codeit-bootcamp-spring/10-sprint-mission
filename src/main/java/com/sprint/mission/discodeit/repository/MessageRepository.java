package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  // 커서 기반 페이지네이션 및  메시지 조회 시 연관 객체도 함께 가져오기 위한 쿼리
  @Query("SELECT m FROM Message m "
      + "LEFT JOIN FETCH m.author a " // 작성자 정보도 함께 가져오기 (없을 수도 있으니 LEFT JOIN)
      + "JOIN FETCH a.userStatus " // 작성자의 상태 정보도 함께 가져오기
      + "LEFT JOIN FETCH a.profile " // 작성자의 프로필 사진도 함께 가져오기 (없을 수도 있으니 LEFT JOIN)
      // 특정 채널에서 커서(시간(:createdAt)) 이전 메시지만 조회
      + "WHERE m.channel.id=:channelId AND m.createdAt < :createdAt")
  Slice<Message> findAllByChannelIdWithAuthor(
      @Param("channelId") UUID channelId,
      @Param("createdAt") Instant createdAt,
      Pageable pageable);

  // 전체 개수 조회 (PageResponse의 totalElements 필드에 사용)
  long countByChannelId(UUID channelId);

  void deleteByChannelId(UUID channelId);

  @Query("SELECT MAX(m.createdAt) FROM Message m WHERE m.channel.id = :channelId")
  Optional<Instant> findLastMessageAtByChannelId(@Param("channelId") UUID channelId);
}
