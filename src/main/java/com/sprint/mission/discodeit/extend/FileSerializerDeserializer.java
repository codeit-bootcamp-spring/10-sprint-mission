package com.sprint.mission.discodeit.extend;

import com.sprint.mission.discodeit.entity.BaseEntity;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public abstract class FileSerializerDeserializer<T extends BaseEntity> {
    private final Class<T> type;
    @Value("${discodeit.repository.file-directory}")
    private String DATA_ROOT_PATH;

    protected FileSerializerDeserializer(Class<T> type) {
        this.type = type;
    }

    protected T save(String filePath, T data) {
        Path file = ensureDirectory(filePath).resolve(String.format("%s.ser", data.getId()));

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
            oos.writeObject(data);
            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Optional<T> load(String filePath, UUID uuid) {
        Path file = resolveDirectory(filePath).resolve(String.format("%s.ser", uuid.toString()));

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
            Object obj = ois.readObject();
            return Optional.ofNullable(type.cast(obj));
        } catch (Exception e) {
            throw new RuntimeException("파일 읽는데 실패함: " + e);
        }
    }

    protected List<T> loadAll(String filePath) {
        Path directory = resolveDirectory(filePath);
        if (Files.notExists(directory)) {
            return new ArrayList<>();
        }

        try (Stream<Path> paths = Files.list(directory)) {
            return paths.map(path -> {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    Object obj = ois.readObject();
                    return type.cast(obj);
                } catch (Exception e) {
                    throw new RuntimeException("파일 읽는데 실패함: " + e);
                }
            }).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void delete(String filePath, UUID uuid) {
        Path file = resolveDirectory(filePath).resolve(String.format("%s.ser", uuid.toString()));

        try {
            Files.delete(file);
        } catch (NoSuchFileException e) {
            throw new IllegalStateException("존재하지 않는 파일입니다.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path ensureDirectory(String filePath) {
        Path directory = resolveDirectory(filePath);

        if (Files.notExists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return directory;
    }

    private Path resolveDirectory(String filePath) {
        return Paths.get(System.getProperty("user.dir"), DATA_ROOT_PATH, filePath);
    }
}
