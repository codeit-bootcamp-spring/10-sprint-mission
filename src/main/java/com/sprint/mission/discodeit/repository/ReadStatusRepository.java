package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  List<ReadStatus> findAllByUserId(UUID userId);

  @Query("""
      SELECT rs
      FROM ReadStatus rs
      JOIN FETCH rs.user
      WHERE rs.channel.id = :channelId
      """)
  List<ReadStatus> findAllByChannelId(UUID channelId);

  @Query("""
      SELECT rs
      FROM ReadStatus rs
      WHERE rs.channel.id = :channelId
        AND rs.notificationEnabled = true
        AND rs.user.id <> :senderId
      """)
  List<ReadStatus> findAllByChannelIdAndNotificationEnabledTrueAndUserIdNot(
      @Param("channelId") UUID channelId,
      @Param("senderId") UUID senderId
  );

  Optional<ReadStatus> findByChannelIdAndUserId(UUID channelId, UUID userId);

  void deleteByUser(User user);

  void deleteByChannel(Channel channel);
}
