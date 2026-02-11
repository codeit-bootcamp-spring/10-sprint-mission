package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@Primary
public class FileBinaryContentRepository implements BinaryContentRepository {
    private static final String FILE_PATH = "binaryContent.dat";
    private Map<UUID, BinaryContent> data;

    public FileBinaryContentRepository() {
        this.data = loadFromFile();
    }

    @Override
    public void save(BinaryContent binaryContent) {
        if (binaryContent == null) throw new IllegalArgumentException("binaryContent는 null일 수 없습니다.");
        if (binaryContent.getId() == null) throw new IllegalArgumentException("binaryContent.id는 null일 수 없습니다.");

        data.put(binaryContent.getId(), binaryContent);
        saveToFile();
    }

    @Override
    public void delete(UUID id) {
        if (id == null) throw new IllegalArgumentException("id는 null일 수 없습니다.");

        data.remove(id);
        saveToFile();
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        if (id == null) throw new IllegalArgumentException("id는 null일 수 없습니다.");

        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(data.values());
    }

    // 파일 I/O


    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("BinaryContent 데이터 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, BinaryContent> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, BinaryContent>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
