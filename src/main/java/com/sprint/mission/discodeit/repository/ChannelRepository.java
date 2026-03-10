package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.channeldto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    @EntityGraph(attributePaths = {"readStatus", "user"})
    boolean existsByName(String name);

    // 채널과 연관 엔티티인 readStatus, user를 fetch join
    @Query("""
                select distinct c
                from Channel c
                join c.readStatuses mine
                left join fetch c.readStatuses rs
                left join fetch rs.user u
                where mine.user.id = :userId
        """)
    List<Channel> findAllParticipatingWithParticipants(@Param("userId") UUID userId);

}
