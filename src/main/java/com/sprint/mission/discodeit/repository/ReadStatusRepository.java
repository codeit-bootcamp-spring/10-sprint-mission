package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  ReadStatus findByUser_IdAndChannel_Id(UUID userId, UUID channelId);

  List<ReadStatus> findAllByUser_Id(UUID userId);

  void deleteByUser_Id(UUID userId);

  void deleteByChannel_Id(UUID channelId);

  @Query("""
          select rs
          from ReadStatus rs
          join fetch rs.user
          where rs.channel.id = :channelId
            and rs.notificationEnabled = true
            and rs.user.id <> :senderId
      """)
  List<ReadStatus> findNotificationTargets(@Param("channelId") UUID channelId,
      @Param("senderId") UUID senderId);

  default ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
    return findByUser_IdAndChannel_Id(userId, channelId);
  }

  default List<ReadStatus> findAllByUserId(UUID userId) {
    return findAllByUser_Id(userId);
  }

  default void deleteByUserId(UUID userId) {
    deleteByUser_Id(userId);
  }

  default void deleteByChannelId(UUID channelId) {
    deleteByChannel_Id(channelId);
  }

  default void delete(UUID id) {
    deleteById(id);
  }
}