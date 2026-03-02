package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.utils.FileLockProvider;
import java.nio.file.Path;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public abstract class FileRepositoryLockSupport {

    private final FileLockProvider fileLockProvider;

    protected FileRepositoryLockSupport(FileLockProvider fileLockProvider) {
        this.fileLockProvider = fileLockProvider;
    }

    protected void withLock(Path path, Runnable action) {
        ReentrantLock lock = fileLockProvider.getLock(path);
        lock.lock();
        try {
            action.run();
        } finally {
            lock.unlock();
        }
    }

    protected <T> T withLock(Path path, Supplier<T> action) {
        ReentrantLock lock = fileLockProvider.getLock(path);
        lock.lock();
        try {
            return action.get();
        } finally {
            lock.unlock();
        }
    }
}
