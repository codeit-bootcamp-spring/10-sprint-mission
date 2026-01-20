package com.sprint.mission.discodeit.service.file;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

public abstract class BaseFileService<T extends Serializable & Identifiable> {
    protected final Path directory;

    protected BaseFileService(Path directory) {
        this.directory = directory;
        init();
    }

    private void init() {
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException("저장소 초기화 실패: " + directory, e);
        }
    }

    protected Path getFilePath(UUID id) {
        return directory.resolve(id.toString() + ".ser");
    }

    protected void save(T data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(getFilePath(data.getId())))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + data.getId(), e);
        }
    }

    protected T load(Path path) {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("파일 로드 실패: " + path.getFileName(), e);
        }
    }

    // 공통 findAll 구현
    public List<T> findAll() {
        try (Stream<Path> stream = Files.list(directory)) {
            return stream
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::load)
                    .toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    // 공통 findById 구현
    public T findById(UUID id) {
        Path path = getFilePath(id);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("데이터를 찾을 수 없습니다: " + id);
        }
        return load(path);
    }
}
