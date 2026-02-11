package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
public class FileUserStatusRepository implements UserStatusRepository {
    private final List<UserStatus> data = new ArrayList<>();

    @Override
    public void save(UserStatus userStatus) {
        data.removeIf(s -> s.getId().equals(userStatus.getId()));
        data.add(userStatus);
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return data.stream().filter(s -> s.getId().equals(id)).findFirst();
    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(s -> s.getId().equals(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return data.stream().filter(s -> s.getUserId().equals(userId)).findFirst();
    }

    @Override
    public void deleteByUserId(UUID userId) {
        data.removeIf(s -> s.getUserId().equals(userId));
    }
}