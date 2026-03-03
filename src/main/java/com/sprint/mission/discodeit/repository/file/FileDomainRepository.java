package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.repository.DomainRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class FileDomainRepository<T> implements DomainRepository<T> {
    protected final Path DIRECTORY;
    protected final String EXTENSION;

    public FileDomainRepository(Path DIRECTORY, String EXTENSION) {
        this.DIRECTORY = DIRECTORY;
        this.EXTENSION = EXTENSION;
        try {
            Files.createDirectories(DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected T save(T entity, Function<T, UUID> idExtractor) {
        Path path = resolvePath(idExtractor.apply(entity));
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(entity);
            return entity;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<T> findById(UUID id) {
        if (existsById(id)) {
            Path path = resolvePath(id);
            return findByPath(path);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(resolvePath(id));
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings(value = "unchecked")
    protected Optional<T> findByPath(Path path) {
        try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            // todo: deserialize to T ...?
            return Optional.of((T) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <R> R streamAll(Function<Stream<T>, R> action) {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            Stream<T> stream = paths.map(this::findByPath)
                    .flatMap(Optional::stream);
            return action.apply(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean anyMatch(Predicate<T> predicate) {
        return streamAll(stream -> stream.anyMatch(predicate));
    }

    @Override
    public Stream<T> filter(Predicate<T> predicate) {
        return streamAll(stream -> stream.filter(predicate));
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }
}
