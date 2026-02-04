package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@Primary
public class FileReadStatusRepository implements ReadStatusRepository {
    private static final String FILE_PATH = "readStatus.dat";
    private Map<UUID, ReadStatus> data;

    public FileReadStatusRepository() {
        this.data = loadFromFile();
    }

    @Override
    public void save(ReadStatus readStatus) {
        if (readStatus == null) throw new IllegalArgumentException("readStatus는 null일 수 없습니다.");
        if (readStatus.getId() == null) throw new IllegalArgumentException("readStatus.id는 null일 수 없습니다.");

        data.put(readStatus.getId(), readStatus);
        saveToFile();
    }
    @Override
    public void delete(UUID readStatusId){
        if(readStatusId == null) throw new IllegalArgumentException("id는 null 일 수 없습니다.");
        data.remove(readStatusId);
        saveToFile();
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId){
        if(readStatusId == null) throw new IllegalArgumentException("id는 null 일 수 없습니다.");
        return Optional.ofNullable(data.get(readStatusId));
    }
    @Override
    public List<ReadStatus> findAll(){
        return new ArrayList<>(data.values());
    }
    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId){
        if (channelId == null) throw new IllegalArgumentException("channelId는 null일 수 없습니다.");

        List<ReadStatus> result = new ArrayList<>(); // 새로 발급
        for(ReadStatus rs : data.values()){
            if(channelId.equals(rs.getChannelId())) {
                result.add(rs);
            }
        }
        return result;
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("userId는 null일 수 없습니다.");

        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus rs : data.values()) {
            if (userId.equals(rs.getUserId())) {
                result.add(rs);
            }
        }
        return result;
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        if (userId == null) throw new IllegalArgumentException("userId는 null일 수 없습니다.");
        if (channelId == null) throw new IllegalArgumentException("channelId는 null일 수 없습니다.");

        for (ReadStatus rs : data.values()) {
            if (userId.equals(rs.getUserId()) && channelId.equals(rs.getChannelId())) {
                return Optional.of(rs);
            }
        }
        return Optional.empty();
    }



    // 파일 I/O


    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("ReadStatus 데이터 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, ReadStatus> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, ReadStatus>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
