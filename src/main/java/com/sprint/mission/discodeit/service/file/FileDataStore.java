package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileDataStore implements Serializable { // 의존성 문제로 한 번에 전체 저장
    private static final long serialVersionUID = 1L;
    private static final Path STORAGE_PATH = Path.of("./FileIOTestStorage/storage.ser");

    private final Map<UUID, User> usersData = new HashMap<>();
    private final Map<UUID, Channel> channelsData = new HashMap<>();
    private final Map<UUID, Message> messagesData = new HashMap<>();

    public Map<UUID, User> getUsersData() {
        return usersData;
    }

    public Map<UUID, Channel> getChannelsData() {
        return channelsData;
    }

    public Map<UUID, Message> getMessagesData() {
        return messagesData;
    }

    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORAGE_PATH.toFile()))) {
            // 변경 사항 발생 시 data 덮어쓰기?
            oos.writeObject(this);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    public static FileDataStore loadData() {
        // 파일이 존재하지 않는다면 빈 Map
        if (!Files.exists(STORAGE_PATH)) {
            return new FileDataStore();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STORAGE_PATH.toFile()))){
            Object fileData = ois.readObject();
            if (fileData instanceof FileDataStore) {
                FileDataStore fileDataStore = (FileDataStore) fileData;
                return fileDataStore;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
        }
        return new FileDataStore(); // 파일이 존재하지만 읽기 실패 시.
    }
}
