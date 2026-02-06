package com.sprint.mission.discodeit.repository.status;

import com.sprint.mission.discodeit.entity.status.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

@Repository
public class UserStatusRepositoryImpl implements UserStatusRepository {

    private final List<UserStatus> data = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public UserStatus save(UserStatus userStatus) {
        lock.lock();
        try {
            data.removeIf(us -> us.getId().equals(userStatus.getId()));
            data.add(userStatus);
            return userStatus;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<UserStatus> findAll() {
        lock.lock();
        try {
            return new ArrayList<>(data); // List.of() -> new ArrayList<>(data)
        }finally{
            lock.unlock();
        }
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        lock.lock();
        try {
            return data.stream()
                    .filter(us -> us.getId().equals(id))
                    .findFirst();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        lock.lock();
        try {
            return data.stream()
                    .filter(us -> us.getUserId().equals(userId))
                    .findFirst();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        lock.lock();
        try {
            return data.stream()
                    .anyMatch(us -> us.getUserId().equals(userId));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteById(UUID id) {
        lock.lock();
        try {
            data.removeIf(us -> us.getId().equals(id));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        lock.lock();
        try {
            data.removeIf(us -> us.getUserId().equals(userId));
        } finally {
            lock.unlock();
        }
    }
}

