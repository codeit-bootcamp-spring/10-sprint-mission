package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.message.LastMessageTimeDto;
import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("select m from Message m left join fetch m.author left join fetch m.author.profile "
      + "where m.channel.id = :channelId "
      + "and (cast(:cursor as timestamp) is null or m.createdAt < :cursor)")
  Slice<Message> findAllByChannelIdFetchUserInfo(UUID channelId, Pageable pageable,
      Instant cursor);//널비교때는 타입 추론 이 안되기 때문에 캐스팅 필요

  @Query("select m from Message m left join fetch m.attachments where m.id in :ids")
  List<Message> findAllByIdInFetchAttachments(List<UUID> ids);

  @Query(value = "SELECT * FROM messages WHERE channel_id = :channelId ORDER BY created_at DESC LIMIT 1",
      nativeQuery = true)
  Optional<Message> findLastMessageByChannelId(@Param("channelId") UUID channelId);

  @Query("select m from Message m join fetch m.author u left join fetch u.profile left join fetch m.attachments where m.id = :id")
  Optional<Message> findByIdFetchAttachmentAndUser(UUID id);

  //채널아이디목록에 대한 모든 마지막 메세지를 조회 -> 인터페이스를 정의해서 반환 자동 매핑
  //별칭 지정 안하면 인터페이스 함수랑 get 뒤에 이름이 같아야함
//  @Query(value = "SELECT channel_id, Max(created_at) AS maxCreatedAt FROM messages "
//      + "WHERE messages.channel_id IN :channelIds "
//      + "GROUP BY channel_id",
//      nativeQuery = true) --h2에서 uuid를 byte로 저장하는 문제 발생 -> 인터페이스 프로젝션에서 dto 프로젝션으로 수정
  @Query(
      "SELECT new com.sprint.mission.discodeit.dto.message.LastMessageTimeDto(m.channel.id, MAX(m.createdAt)) "
          + "FROM Message m "
          + "WHERE m.channel.id IN :channelIds "
          + "GROUP BY m.channel.id")
  List<LastMessageTimeDto> findAllLastMessagesByChannelId(Set<UUID> channelIds);

  @Modifying//벌크쿼리 적용
  @Query("DELETE FROM Message m WHERE m.channel.id = :channelId")
  void bulkDeleteByChannelId(UUID channelId);

}
