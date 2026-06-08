package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.channeldto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    @EntityGraph(attributePaths = {"readStatuses"})
    boolean existsByName(String name);

    // 채널과 연관 엔티티인 readStatus, user를 fetch join
    @Query("""
                select distinct c
                from Channel c
                left join fetch c.readStatuses rs
                left join fetch rs.user u
                where c.type = :publicType
                   or exists (
                      select 1
                      from ReadStatus mine
                      where mine.channel = c
                        and mine.user.id = :userId
                   )
        """)
    List<Channel> findAllVisibleWithParticipants(@Param("userId") UUID userId,
        @Param("publicType") ChannelType publicType);

    @EntityGraph(attributePaths = {
        "readStatuses", "readStatuses.user", "readStatuses.user.profile"
    })
    Optional<Channel> findWithParticipantsById(UUID id);

}
