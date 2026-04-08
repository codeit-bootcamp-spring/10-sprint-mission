package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  List<ReadStatus> findAllByUserId(UUID userId);

  List<ReadStatus> findAllByChannelId(UUID channelId);

  boolean existsByChannelIdAndUserId(UUID channelId, UUID userId);

  void deleteAllByChannelId(UUID channelId);

  @Query("SELECT rs FROM ReadStatus rs JOIN FETCH User u ON rs.user.id = u.id WHERE rs.channel.id IN :channelIds")
  List<ReadStatus> findAllByChannelIdIn(Collection<UUID> channelIds);
}
