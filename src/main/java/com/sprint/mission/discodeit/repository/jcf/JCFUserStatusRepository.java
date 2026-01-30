package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFUserStatusRepository implements UserStatusRepository {

    private final List<UserStatus> data = new ArrayList<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        delete(userStatus);
        data.add(userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID userStatusId) {
        return data.stream()
                .filter(status -> status.getId().equals(userStatusId))
                .findFirst();
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return data.stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return List.copyOf(data);
    }

    @Override
    public void delete(UserStatus userStatus) {
        data.removeIf(status -> status.equals(userStatus));
    }
}
