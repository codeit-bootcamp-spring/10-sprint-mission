package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  List<ReadStatus> findAllByUserId(UUID userId);

  @Query("select r from ReadStatus r join fetch r.user where r.channel.id = :channelId")
  List<ReadStatus> findAllByChannelIdFetchUser(UUID channelId);

  @Query("select r from ReadStatus r join fetch r.user left join fetch r.user.userStatus "
      + "left join fetch r.user.profile where r.channel.id in :channelIds")
  List<ReadStatus> findAllByChannelIdInFetchUser(Set<UUID> channelIds);

  @Query("select r from ReadStatus r join fetch r.channel where r.user.id = :userId")
  List<ReadStatus> findAllByUserIdFetchChannel(UUID userId);

  Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

  @Modifying
  @Query("delete from ReadStatus r where r.user.id = :userId")
  void deleteByUserId(UUID userId);
}
