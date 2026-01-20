package com.sprint.mission.service.file;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseFileService {
    protected final Path directory;

    public BaseFileService(Path directory) {
        this.directory = directory;
    }

    public <T> void save(Path filePath, T data) { // 동일한 파일경로가 있다면 덮어쓰기
        try (
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()));
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
            throw new RuntimeException("삭제 중 오류가 발생했습니다.");
        }
    }

    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> load(Function<V, K> keyMapper) { // value에서 key를 뽑아온다.
        if (!Files.exists(directory)) {
            return new HashMap<>();
        }
        try {
            return Files.list(directory)
                    .map(path -> {
                        try (
                                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
                        ) {
                            Object data = ois.readObject();
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
