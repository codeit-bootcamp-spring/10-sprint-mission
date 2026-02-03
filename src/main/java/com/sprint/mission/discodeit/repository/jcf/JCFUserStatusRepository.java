package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf"
)
public class JCFUserStatusRepository implements UserStatusRepository {
    private final HashMap<UUID, UserStatus> data;

    public JCFUserStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        this.data.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }

    @Override
    public void deleteByUserId(UUID userID) {
        deleteById(findAll().stream()
                .filter(userStatus -> userID.equals(userStatus.getUserID()))
                .findFirst().get().getId());
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID id) {
        return findById(
                        findAll()
                                .stream()
                                .filter(userStatus -> id.equals(userStatus.getUserID()))
                                .findFirst().get().getId());
    }


    @Override
    public List<UserStatus> findAll() {
        return this.data.values().stream().toList();
    }
}
