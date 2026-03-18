package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    @EntityGraph(attributePaths = {
            "readStatuses",
            "readStatuses.user",
            "readStatuses.user.status",
            "readStatuses.user.profile"
    })
    @Query("""
            select c
            from Channel c
            where c.type = :publicType
               or (
                    c.type = :privateType
                    and exists (
                        select rs.id
                        from ReadStatus rs
                        where rs.channel = c
                          and rs.user.id = :requesterId
                    )
               )
            """)
    List<Channel> findVisibleChannels(
            UUID requesterId,
            ChannelType publicType,
            ChannelType privateType
    );
}
