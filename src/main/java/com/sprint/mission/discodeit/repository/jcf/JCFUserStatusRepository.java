package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf"
)
public class JCFUserStatusRepository extends JCFDomainRepository<UserStatus> implements UserStatusRepository {

    public JCFUserStatusRepository() {
        super(new HashMap<>());
    }

    @Override
    public UserStatus save(UserStatus entity) {
        getData().put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return filter(status -> status.matchUserId(userId)).findFirst();
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return anyMatch(status -> status.matchUserId(userId));
    }

    @Override
    public List<UserStatus> findAll() throws IOException {
        return streamAll(Stream::toList);
    }
}
