package com.sprint.mission.discodeit.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileIOHelper {
    private static final Path BASE_DIRECTORY =
            Paths.get(System.getProperty("user.dir"), "data");

    public static Path resolveDirectory(String name) {
        Path path = BASE_DIRECTORY.resolve(name);
        init(path);

        return path;
    }

    private static void init(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException("디렉토리 생성을 실패하였습니다 directory: : " + directory, e);
            }
        }
    }

    public static <T> void save(Path filePath, T data) {
        try (
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장을 실패하였습니다. filePath: " + filePath, e);
        }
    }

    public static <T> Optional<T> load(Path directory) {
        if (!Files.exists(directory)) {
            return Optional.empty();
        }

        try (
                FileInputStream fis = new FileInputStream(directory.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return Optional.of((T) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("파일 로드를 실패하였습니다 directory: " + directory, e);
        }
    }

    public static <T> List<T> loadAll(Path directory) {
        if (Files.notExists(directory)) {
            return new ArrayList<>();
        }

        try {
            return Files.list(directory)
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (T) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("파일 로드를 실패하였습니다 path: " + path, e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 조회를 실패하였습니다 directory: " + directory, e);
        }
    }

    public static void delete(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제를 실패하였습니다. filePath: " + filePath, e);
        }
    }
}
