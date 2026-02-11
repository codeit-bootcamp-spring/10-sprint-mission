package com.sprint.mission.discodeit.userstatus.repository;

import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
public class JCFUserStatusRepository implements UserStatusRepository {
    private final List<UserStatus> data = new ArrayList<>();

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return data.stream()
                .filter(us -> us.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return data.stream()
                .filter(us -> us.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return List.copyOf(data);
    }

    @Override
    public void save(UserStatus userStatus) {
        if(data.contains(userStatus))
            data.set(data.indexOf(userStatus), userStatus);
        else
            data.add(userStatus);
    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(us -> us.getId().equals(id));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        data.removeIf(us -> us.getUserId().equals(userId));
    }
}
