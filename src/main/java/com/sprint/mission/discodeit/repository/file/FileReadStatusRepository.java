package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {
    private static final String READ_STATUS_FILE = "data/readStatus.ser";

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
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(READ_STATUS_FILE))){
            return (Map<UUID,ReadStatus>) ois.readObject();
        }
        catch(Exception e){
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, ReadStatus> data){
        try(ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(READ_STATUS_FILE)))){
            oos.writeObject(data);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
