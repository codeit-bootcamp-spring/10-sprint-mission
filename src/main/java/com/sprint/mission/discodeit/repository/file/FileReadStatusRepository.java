package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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
public class FileReadStatusRepository implements ReadStatusRepository {
    private final String filePath;
    public FileReadStatusRepository(@Value("${discodeit.repository.file-directory}") String directory){
        this.filePath = directory + "/readStatus.ser";
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {

        return Optional.ofNullable(loadData().get(id));
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(loadData().values());
    }

    @Override
    public void save(ReadStatus readStatus) {
        Map<UUID, ReadStatus> data = loadData();
        data.put(readStatus.getId(), readStatus);
        saveData(data);

    }

    @Override
    public void delete(UUID id) {
        Map<UUID, ReadStatus> data = loadData();
        data.remove(id);
        saveData(data);
    }

    private Map<UUID, ReadStatus> loadData(){
        File file = new File(filePath);
        if(!file.exists()) return new HashMap<>();
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            return (Map<UUID,ReadStatus>) ois.readObject();
        }
        catch(Exception e){
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, ReadStatus> data){
        File file = new File(filePath);
        if(file.getParentFile() != null && !file.getParentFile().exists()){
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
