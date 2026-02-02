package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "file", matchIfMissing = true)
public class FileReadStatusRepository implements ReadStatusRepository {
    private List<ReadStatus> readStatusData;
    private final Path StoreFile;

    public FileReadStatusRepository(@Value("${discodeit.repository.path}") String directoryPath) {
        Path BasePath = Paths.get(directoryPath).resolve("readStatus");
        this.StoreFile = BasePath.resolve("readStatus.ser");
        init(BasePath);
        loadData();
    }

    private void init(Path BasePath) {
        try {
            if(!Files.exists(BasePath)) {
                Files.createDirectories(BasePath);
            }
        } catch(Exception e) {
            System.out.println("Directory creation failed." + e.getMessage());
        }
    }

    void saveData() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(StoreFile.toFile()))) {
            oos.writeObject(readStatusData);
        } catch (Exception e) {
            throw new RuntimeException("Data save failed." + e.getMessage());
        }
    }

    private void loadData() {
        if(!Files.exists(StoreFile)) {
            readStatusData = new ArrayList<>();
            return;
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(StoreFile.toFile()))) {
            readStatusData = (List<ReadStatus>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Data load failed." + e.getMessage());
        }
    }

    @Override
    public Optional<ReadStatus> find(UUID readStatusID){
        loadData();
        return readStatusData.stream()
                .filter(readStatus -> readStatus.getId().equals(readStatusID))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findByUserID(UUID userID){
        loadData();
        return readStatusData.stream()
                .filter(readStatus -> readStatus.getUserID().equals(userID))
                .toList();
    }

    @Override
    public List<ReadStatus> findAll(){
        loadData();
        return readStatusData;
    }

    @Override
    public ReadStatus save(ReadStatus readStatus){
        loadData();
        for (int i = 0; i < readStatusData.size(); i++){
            if(readStatusData.get(i).getId().equals(readStatus.getId())){
                readStatusData.set(i, readStatus);
                saveData();
                return readStatus;
            }
        }
        readStatusData.add(readStatus);
        saveData();
        return readStatus;
    }

    @Override
    public void delete(UUID readStatusID){
        loadData();
        readStatusData.removeIf(readStatus -> readStatus.getId().equals(readStatusID));
        saveData();
    }

    @Override
    public void deleteByChannelID(UUID channelID){
        loadData();
        readStatusData.removeIf(readStatus -> readStatus.getChannelID().equals(channelID));
        saveData();
    }

    @Override
    public void deleteByChannelIDAndUserID(UUID channelID, UUID userID){
        loadData();
        readStatusData.removeIf(readStatus -> readStatus.getChannelID().equals(channelID) && readStatus.getUserID().equals(userID));
        saveData();
    }
}
