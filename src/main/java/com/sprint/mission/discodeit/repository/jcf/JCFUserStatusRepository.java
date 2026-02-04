package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.val;

import java.util.*;

public class JCFUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> data;

    public JCFUserStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public void save(UserStatus userStatus) {
        this.data.put(userStatus.getId(), userStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        this.data.get(userStatusId);
    }

    @Override
    public List<UserStatus> loadAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public UserStatus loadById(UUID userStatusId) {
        return this.data.get(userStatusId);
    }

    @Override
    public UserStatus loadByUserId(UUID userId) {
        for (UserStatus userStatus : this.data.values()) {
            if (userStatus.getUserId().equals(userId)) {
                return userStatus;
            }
        }
        throw new NoSuchElementException();
    }
}
