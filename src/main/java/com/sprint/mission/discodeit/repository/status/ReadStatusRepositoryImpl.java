package com.sprint.mission.discodeit.repository.status;

import com.sprint.mission.discodeit.entity.status.ReadStatus;
import org.springframework.stereotype.Repository;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ReadStatusRepositoryImpl implements ReadStatusRepository {

    private final List<ReadStatus> data = new ArrayList<>();
    private final Path filePath;

    public ReadStatusRepositoryImpl() {
        this.filePath = Path.of("data","readStatuse.ser");
        load();
    }

    private void load(){
        if(Files.notExists(filePath)){
            return;
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))){
            @SuppressWarnings("unchecked")
            List<ReadStatus>loaded = (List<ReadStatus>) ois.readObject();
            data.clear();
            data.addAll(loaded);
        } catch (InvalidClassException e) {
            System.out.println("ReadStatus 이전 버전 파일을 무시하고 새로 시작합니다" + e.getMessage());
            data.clear();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("ReadStatus 데이터 로드 실패",e);
        }
    }

    private void save(){
        try {
            Files.createDirectories(filePath.getParent());
            try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))){
                oos.writeObject(data);
            }
        }catch (IOException e) {
            throw new RuntimeException("ReadStatus 데이터 저장 실패",e);
        }
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        // 기존 데이터가 있으면 제거 (update)
        data.removeIf(rs-> rs.getId().equals(readStatus.getId()));
        data.add(readStatus);
        save();
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id){
        return data.stream()
                .filter(rs -> rs.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId){
        return data.stream()
                .filter(rs -> rs.getUserId().equals(userId)&&rs.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findByUserId(UUID userId){
        return data.stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadStatus> findByChannelId(UUID channelId){
        return data.stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(rs -> rs.getId().equals(id));
        save();
    }

}
