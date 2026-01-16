package com.sprint.mission.repository.file;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseFileRepository {
    protected final Path directory;

    protected BaseFileRepository(Path directory) {
        this.directory = directory;
    }

    public <T> void save(Path filePath, T data) {
        try (
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> load(Function<V, K> keyMapper) {
        if (!Files.exists(directory)) {
            return new HashMap<>();
        }
        try {
            return Files.list(directory) // 디렉토리 안의 모든 파일을 나열
                    .map(path -> { // 파일 하나씩 읽어서 객체로 변환
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis);
                        ) {
                            Object data = ois.readObject(); // 파일을 읽을 때마다 새 객체 생성
                            return (V) data; // 객체 하나를 읽어옴
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toMap(keyMapper, v -> v));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
