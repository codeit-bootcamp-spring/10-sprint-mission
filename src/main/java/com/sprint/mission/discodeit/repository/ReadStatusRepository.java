package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {


    List<ReadStatus> findAllByUserId(UUID id);

    @Query("""
                select rs 
                from ReadStatus rs 
                join fetch rs.user u 
                where rs.channel.id in :channelIds
        """)
    List<ReadStatus> findAllWithUserByChannelIds(@Param("channelIds") Collection<UUID> channelIds);

    @Query("""
        SELECT rs
                from ReadStatus rs
                join fetch rs.user
                where rs.channel.id = :id
        """)
    List<ReadStatus> findAllByChannelId(UUID id);

    List<ReadStatus> findAllByChannelIdAndNotificationEnabledTrue(UUID channelId);


    List<ReadStatus> findAllByChannelIdIn(List<UUID> channelIds);
}
