package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Repository
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
        return getData().values()
                .stream()
                .filter(status -> status.matchUserId(userId))
                .findFirst();
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return getData().values()
                .stream()
                .anyMatch(status -> status.matchUserId(userId));
    }
}
