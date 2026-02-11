package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf"
)
public class JCFReadStatusRepository extends JCFDomainRepository<ReadStatus> implements ReadStatusRepository {

    public JCFReadStatusRepository() {
        super(new HashMap<>());
    }

    @Override
    public ReadStatus save(ReadStatus entity) {
        getData().put(entity.getId(), entity);
        return entity;
    }

    @Override
    public boolean existsByUserAndChannelId(UUID userId, UUID channelId) {
        return filter(status -> status.matchUserId(userId))
                .anyMatch(status -> status.matchChannelId(channelId));
    }
}
