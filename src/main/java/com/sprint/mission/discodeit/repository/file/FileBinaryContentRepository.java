package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final File file;
    private final Map<UUID, BinaryContent> cache;

    public FileBinaryContentRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String directory) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.file = new File(dir, "binary_contents.dat");
        this.cache = loadData();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, BinaryContent> loadData() {
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, BinaryContent>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(cache);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(BinaryContent binaryContent) {
        cache.put(binaryContent.getId(), binaryContent);
        saveData();
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(cache::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void delete(UUID id) {
        cache.remove(id);
        saveData();
    }
}
