package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Common;

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

public abstract class FileSerDe<T extends Common> {
    public T save(String filePath, T data) {
        Path file = checkDirectory(filePath).resolve(String.format("%s.ser", data.getId()));

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
            oos.writeObject(data);
            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Optional<T> load(String filePath, UUID uuid, Class<T> type) {
        Path path = Paths.get(filePath).resolve(String.format("%s.ser", uuid.toString()));

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            Object obj = ois.readObject();
            return Optional.ofNullable(type.cast(obj));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<T> loadAll(String filePath, Class<T> type) {
        Path directory = Paths.get(filePath);
        if (Files.notExists(directory)) {
            return new ArrayList<>();
        }

        try (Stream<Path> paths = Files.list(directory)){
            return paths.map(path -> {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    Object obj = ois.readObject();
                    return type.cast(obj);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String filePath, UUID uuid) {
        Path path = Paths.get(filePath).resolve(String.format("%s.ser", uuid.toString()));

        try {
            Files.delete(path);
        } catch (NoSuchFileException e) {
            throw new IllegalStateException("존재하지 않는 파일입니다.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path checkDirectory(String filePath) {
        Path directory = Paths.get(System.getProperty("user.dir"), filePath);

        if (Files.notExists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return directory;
    }
}
