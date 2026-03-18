package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    @Query("""
            select (count(rs) > 0)
            from ReadStatus rs
            where rs.user.id = :userId
              and rs.channel.id = :channelId
            """)
    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    @EntityGraph(attributePaths = {"user", "channel"})
    List<ReadStatus> findAllByUser(User user);

    @EntityGraph(attributePaths = {"user", "channel"})
    List<ReadStatus> findByChannelIn(List<Channel> channels);
}
