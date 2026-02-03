package com.sprint.mission.discodeit.repository.file;

import java.io.*;
import java.util.*;

public abstract class BaseFileRepository<T extends Serializable> {
    private final String filePath;

    protected  BaseFileRepository(String filePath){
        this.filePath = filePath;
    }

    // 파일 쓰기 (직렬화)
    protected void saveData(Map<UUID, T> data){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))){
            oos.writeObject(data);
        } catch (IOException e){
            System.err.println("[파일 저장 실패]: " + e.getMessage());
        }
    }

    // 파일 읽기 (역직렬화)
    protected Map<UUID, T> loadData() {
        File file = new File(filePath);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("[파일 로드 실패] 파일이 손상되었거나 형식이 맞지 않습니다: " + filePath, e);
        }
    }
}
