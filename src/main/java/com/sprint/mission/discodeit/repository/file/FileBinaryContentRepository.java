package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file",
        matchIfMissing = false
)
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final String filePath;

    public FileBinaryContentRepository(@Value("${discodeit.repository.file-directory}")String directory){
        this.filePath = directory + "/binaryContent.ser";
    }


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
        File file = new File(filePath); // 👈 여기를 filePath로 변경
        if(!file.exists()) return new HashMap<>();

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            return (Map<UUID,BinaryContent>) ois.readObject();
        }
        catch(Exception e){
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, BinaryContent> data){
        File file = new File(filePath);
        if(file.getParentFile() != null && file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        try(ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(file)))){
            oos.writeObject(data);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
