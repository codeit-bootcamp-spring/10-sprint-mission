package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository

public class FileUserStatusRepository implements UserStatusRepository {
    private static final String FILE_PATH = "userStatus.dat";
    private Map<UUID, UserStatus> data;

    public FileUserStatusRepository() {
        this.data = loadFromFile();
    }

    @Override
    public void save(UserStatus userStatus){
        if (userStatus == null) throw new IllegalArgumentException("userStatus 는 null일 수 없습니다.");
        if(userStatus.getId() == null) throw new IllegalArgumentException("userStatusId는 null일 수 없습니다.");
        data.put(userStatus.getId(), userStatus);
        saveToFile();
    }

    @Override
    public void delete(UUID userStatusId) {
        if(userStatusId == null) throw new IllegalArgumentException("userStatusId는 null일 수 없습니다.");
        data.remove(userStatusId);
        saveToFile();
    }
    @Override
    public Optional<UserStatus> findById(UUID userStatusId) {
        if(userStatusId == null) throw new IllegalArgumentException("userStatusId는 null일 수 없습니다.");
        return Optional.ofNullable(data.get(userStatusId));
    }
    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        if(userId==null) throw new IllegalArgumentException("userId는 null일 수 없습니다.");
        for(UserStatus us : data.values()){
            if(userId.equals(us.getId())) {
                return Optional.of(us);
            }
        }
        return Optional.empty();
    }



    // 파일 I/O
    // --------------------

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 데이터 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, UserStatus> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, UserStatus>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}

