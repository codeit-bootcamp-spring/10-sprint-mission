package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> data;
    private final FileObjectStore fileObjectStore;

    public FileUserStatusRepository(FileObjectStore fileObjectStore) {
        this.data = fileObjectStore.getUserStatusesData();
        this.fileObjectStore = fileObjectStore;
    }

    @Override
    public void save(UserStatus userStatus) {
        data.put(userStatus.getId(), userStatus);
        fileObjectStore.saveData();
    }

    @Override
    public Optional<UserStatus> findById(UUID userStatusId) {
        return Optional.ofNullable(data.get(userStatusId));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return data.values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID userStatusId) {
        data.remove(userStatusId);
        fileObjectStore.saveData();
    }

    @Override
    public boolean existUserStatus(UUID userId) {
        return data.values().stream()
                .anyMatch(userStatus -> userStatus.getUserId().equals(userId));
    }
}
