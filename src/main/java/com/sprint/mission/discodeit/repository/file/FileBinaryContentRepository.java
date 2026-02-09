package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.io.*;
import java.util.*;

public class FileBinaryContentRepository implements BinaryContentRepository {
    private static final String FILE_PATH = "binary_content.ser";
    private Map<UUID, BinaryContent> data;

    public FileBinaryContentRepository() {
        this.data = load();
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        data.put(binaryContent.getId(), binaryContent);
        saveToFile();
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID binaryContentId) {
        return Optional.ofNullable(data.get(binaryContentId));
    }

    @Override
    public List<BinaryContent> findAllById(List<UUID> binaryContentIds) {
        return binaryContentIds.stream()
                .map(data::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public BinaryContent findByMessageId(UUID messageId) {
        return data.values().stream()
                .filter(binaryContent -> binaryContent.getMessageId().equals(messageId))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public List<BinaryContent> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public void deleteById(UUID binaryContentId) {
        data.remove(binaryContentId);
        saveToFile();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, BinaryContent> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, BinaryContent>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("파일 데이터 로드 실패", e);
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
