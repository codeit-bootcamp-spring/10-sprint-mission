package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.io.*;
import java.util.*;

public class AbstractFileRepository <T extends BaseEntity> {
    protected final File file;    // 클래스가 사용할 파일 저장소 객체 - 경로를 생성자에서 주입해 저장/불러오기 사용

    protected AbstractFileRepository(String path) {
        this.file = new File(path);

        File parentDir = file.getParentFile();  // 부모 디렉토리
        if (parentDir != null && !parentDir.exists()) { // 부모 디렉토리가 없다면
            parentDir.mkdirs(); // 디렉토리 생성
        }
    }

    public T save(T entity){
        Map<UUID, T> data = load();
        data.put(entity.getId(), entity);
        writeToFile(data);
        return entity;
    }

    public Optional<T> findById(UUID id) {
        Map<UUID, T> data = load();
        return Optional.ofNullable(data.get(id));
    }

    public void delete(UUID id) {
        Map<UUID, T> data = load();
        data.remove(id);
        writeToFile(data);
    }

    public List<T> findAll() {
        Map<UUID, T> data = load();
        return List.copyOf(data.values());
    }

    public void clear() {
        writeToFile(new HashMap<UUID, User>());
    }

    protected String getPath(){
        return file.getPath();
    }

    @SuppressWarnings("unchecked")  // Object형을 Map으로 형변환할 때 뜨는 경고 억제
    protected Map<UUID, T> load() {
        if (!file.exists()) {
            return new HashMap<>(); // 파일 없으면 빈 저장소
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, T>) ois.readObject();   // 파일 읽고 변환, 바이트를 메모리 객체로 복원
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void writeToFile(Map<UUID, ?> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
