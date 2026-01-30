package com.sprint.mission.discodeit.repository.file;


import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {

    private static final String FILE_PATH = "binaryContent.dat";

    private Map<UUID, BinaryContent> loadBinaryFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, BinaryContent>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveBinaryFile(Map<UUID, BinaryContent> map) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        Map<UUID, BinaryContent> map = loadBinaryFile();
        return ids.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, BinaryContent> map = loadBinaryFile();
        map.remove(id);
        saveBinaryFile(map);
    }
}

