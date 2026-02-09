package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.function.ThrowingFunction;
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

    public FileDomainRepository(Path DIRECTORY, String EXTENSION) throws IOException {
        this.DIRECTORY = DIRECTORY;
        this.EXTENSION = EXTENSION;
        Files.createDirectories(DIRECTORY);
    }

    protected T save(T entity, Function<T, UUID> idExtractor) throws IOException {
        Path path = resolvePath(idExtractor.apply(entity));
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(entity);
            return entity;
        }
    }

    @Override
    public Optional<T> findById(UUID id) throws IOException, ClassNotFoundException {
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
    public void deleteById(UUID id) throws IOException {
        Files.deleteIfExists(resolvePath(id));
    }

    @SuppressWarnings(value = "unchecked")
    protected Optional<T> findByPath(Path path) throws IOException, ClassNotFoundException {
        try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return Optional.of((T) ois.readObject());
        }
    }

    @Override
    public  <R> R streamAll(Function<Stream<T>, R> action) throws IOException {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            Stream<T> stream = paths.map(ThrowingFunction.unchecked(this::findByPath))
                    .flatMap(Optional::stream);
            return action.apply(stream);
        }
    }

    @Override
    public boolean anyMatch(Predicate<T> predicate) throws IOException {
        return streamAll(stream -> stream.anyMatch(predicate));
    }

    @Override
    public Stream<T> filter(Predicate<T> predicate) throws IOException {
        return streamAll(stream -> stream.filter(predicate));
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }
}
