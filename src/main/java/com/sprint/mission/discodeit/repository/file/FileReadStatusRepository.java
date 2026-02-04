package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository("readStatusRepository")
public class FileReadStatusRepository implements ReadStatusRepository {
    private List<ReadStatus> data;

    public FileReadStatusRepository() {
        this.data = new ArrayList<>();
    }

    // 직렬화
    public void serialize(List<ReadStatus> readStatuses) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("readStatusList.ser"))) {
            oos.writeObject(readStatuses);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 역직렬화
    public List<ReadStatus> deserialize() {
        File file = new File("readStatusList.ser");
        List<ReadStatus> newReadStatuses = new ArrayList<>();

        if (!file.exists()) {
            return newReadStatuses;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            newReadStatuses = (List<ReadStatus>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("역직렬화가 안됨");
        }
        return newReadStatuses;
    }

    @Override
    public void save(ReadStatus readStatus) {
        this.data.add(readStatus);
        serialize(this.data);
    }

    @Override
    public void delete(UUID id) {
        this.data = deserialize();
        for (ReadStatus readStatus : this.data) {
            if (readStatus.getId().equals(id)) {
                this.data.remove(readStatus);
                serialize(this.data);
                break;
            }
        }
    }

    public void deleteByChannelId(UUID channelId) {
        this.data = deserialize();
        for (ReadStatus readStatus : this.data) {
            if (readStatus.getChannelId().equals(channelId)) {
                this.data.remove(readStatus);
                serialize(this.data);
                break;
            }
        }
    }

    @Override
    public List<ReadStatus> loadAll() {
        return deserialize();
    }

    @Override
    public ReadStatus loadById(UUID readStatusId) {
        this.data = deserialize();
        for(ReadStatus readStatus : this.data) {
            if (readStatus.getId().equals(readStatusId)) {
                return readStatus;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public List<ReadStatus> loadAllByChannelId(UUID channelId) {
        this.data = deserialize();
        List<ReadStatus> readStatusList = new ArrayList<>();
        for(ReadStatus readStatus : this.data) {
            if (readStatus.getChannelId().equals(channelId)) {
                readStatusList.add(readStatus);
            }
        }
        return readStatusList;
    }
}
