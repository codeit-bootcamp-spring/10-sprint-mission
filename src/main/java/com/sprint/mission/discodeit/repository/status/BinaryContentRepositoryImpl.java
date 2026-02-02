package com.sprint.mission.discodeit.repository.status;


import com.sprint.mission.discodeit.entity.status.BinaryContent;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

@Repository
public class BinaryContentRepositoryImpl implements BinaryContentRepository {

    private final List<BinaryContent> data = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public BinaryContent save(BinaryContent content) {
        lock.lock();
        try {
            data.removeIf(bc -> bc.getId().equals(content.getId()));
            data.add(content);
            return content;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        lock.lock();
        try {
            return data.stream()
                    .filter(bc -> bc.getId().equals(id))
                    .findFirst();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean existsById(UUID id) {
        lock.lock();
        try {
            return data.stream()
                    .anyMatch(bc -> bc.getId().equals(id));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteById(UUID id) {
        lock.lock();
        try {
            data.removeIf(bc -> bc.getId().equals(id));
        } finally {
            lock.unlock();
        }
    }
}
