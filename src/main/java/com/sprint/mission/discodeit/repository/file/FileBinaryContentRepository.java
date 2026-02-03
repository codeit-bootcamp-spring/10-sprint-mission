package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {

    private static final String FILE_PATH = "binary_contents.dat";
    private static final String USERS_FILE_PATH = "users.dat";
    private static final String MESSAGES_FILE_PATH = "messages.dat";

    private final Map<UUID, BinaryContent> data;

    public FileBinaryContentRepository() {
        this.data = loadFromFile(FILE_PATH);
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        data.put(binaryContent.getId(), binaryContent);
        saveToFile(FILE_PATH, data);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        List<BinaryContent> result = new ArrayList<>();
        for (UUID id : ids) {
            BinaryContent bc = data.get(id);
            if (bc != null) result.add(bc);
        }
        return result;
    }

    @Override
    public Optional<BinaryContent> findByUserId(UUID userId) {
        if (userId == null) return Optional.empty();

        Map<UUID, User> users = loadFromFile(USERS_FILE_PATH);
        User user = users.get(userId);
        if (user == null) return Optional.empty();

        UUID binaryContentId = user.getBinaryContentId();
        if (binaryContentId == null) return Optional.empty();

        return findById(binaryContentId);
    }

    @Override
    public List<BinaryContent> findAllByMessageId(UUID messageId) {
        if (messageId == null) return List.of();

        Map<UUID, Message> messages = loadFromFile(MESSAGES_FILE_PATH);
        Message message = messages.get(messageId);
        if (message == null) return List.of();

        List<UUID> ids = message.getBinaryContentIds();
        if (ids == null || ids.isEmpty()) return List.of();

        return findAllByIdIn(ids);
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
        saveToFile(FILE_PATH, data);
    }

    private static void saveToFile(String path, Object obj) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            throw new IllegalStateException(path + " 저장 실패", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Map<UUID, T> loadFromFile(String path) {
        File file = new File(path);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            return (obj instanceof Map) ? (Map<UUID, T>) obj : new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }
}