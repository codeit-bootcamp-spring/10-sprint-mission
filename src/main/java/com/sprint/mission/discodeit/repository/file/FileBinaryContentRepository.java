package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {
    private static final String BINARY_CONTENT_FILE = "data/binaryContent.ser";

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(loadData().get(id));
    }

    @Override
    public Optional<BinaryContent> findByUserId(UUID userId) {
        return loadData().values().stream()
                .filter(bc -> bc.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<BinaryContent> findByMessageId(UUID messageId) {
        return loadData().values().stream()
                .filter(bc -> bc.getMessageId().equals(messageId))
                .toList();
    }

    @Override
    public List<UUID> findAll() {
        Map<UUID, BinaryContent> data = loadData();
        return new ArrayList<>(data.keySet());
    }

    @Override
    public void save(BinaryContent binaryContent) {
        Map<UUID, BinaryContent> data = loadData();
        data.put(binaryContent.getId(), binaryContent);
        saveData(data);
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, BinaryContent> data = loadData();
        data.remove(id);
        saveData(data);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, BinaryContent> data = loadData();
        data.values().removeIf(bc -> bc.getUserId().equals(userId));
        saveData(data);
    }

    @Override
    public void deleteByMessageId(UUID messageId) {
        Map<UUID, BinaryContent> data = loadData();
        data.values().removeIf(bc -> bc.getMessageId().equals(messageId));
        saveData(data);
    }

    private Map<UUID, BinaryContent> loadData(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BINARY_CONTENT_FILE))){
            return (Map<UUID,BinaryContent>) ois.readObject();
        }
        catch(Exception e){
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, BinaryContent> data){
        try(ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(BINARY_CONTENT_FILE)))){
            oos.writeObject(data);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
