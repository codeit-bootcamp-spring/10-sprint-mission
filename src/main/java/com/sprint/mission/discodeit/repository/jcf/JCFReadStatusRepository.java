package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
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
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return filter(status -> status.matchChannelId(channelId)).toList();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return filter(status -> status.matchUserId(userId)).toList();
    }

    @Override
    public boolean existsByUserAndChannelId(UUID userId, UUID channelId) {
        return filter(status -> status.matchUserId(userId))
                .anyMatch(status -> status.matchChannelId(channelId));
    }
}
