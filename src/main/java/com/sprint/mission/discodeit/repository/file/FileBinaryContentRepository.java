package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileBinaryContentRepository implements BinaryContentRepository {

    private final Path filePath;

    public FileBinaryContentRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}") String fileDirectory
    ) {
        try {
            Files.createDirectories(Paths.get(fileDirectory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.filePath = Paths.get(fileDirectory, "binaryContent.dat");
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, BinaryContent> loadBinaryFile() {
        File file = filePath.toFile();
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, BinaryContent>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveBinaryFile(Map<UUID, BinaryContent> map) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetFile() {
        saveBinaryFile(new LinkedHashMap<>());
    }

    @Override
    public BinaryContent save(BinaryContent content) {
        Map<UUID, BinaryContent> map = loadBinaryFile();
        map.put(content.getId(), content);
        saveBinaryFile(map);
        return content;
    }

    @Override
    public BinaryContent findById(UUID id) {
        return loadBinaryFile().get(id);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        Map<UUID, BinaryContent> map = loadBinaryFile();
        return ids.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        if (id == null) return;

        Map<UUID, BinaryContent> map = loadBinaryFile();
        map.remove(id);
        saveBinaryFile(map);
    }
}
